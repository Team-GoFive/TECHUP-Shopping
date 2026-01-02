/*********************************
 * 전역 상태
 *********************************/
let userStomp = null;
let adminStomp = null;
let conversationId = null;
let loggedIn = false;
let accessToken = null;

/*********************************
 * 공통 fetch (JWT 자동 포함)
 *********************************/
function authFetch(url, options = {}) {
    return fetch(url, {
        ...options,
        headers: {
            ...(options.headers || {}),
            Authorization: `Bearer ${accessToken}`,
        },
    });
}

/*********************************
 * 로그인
 *********************************/
loginBtn.onclick = async () => {
    const id = loginId.value;
    const pw = loginPw.value;

    const res = await fetch("/api/auth/login", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({email: id, password: pw}),
    });

    if (!res.ok) {
        loginStatus.textContent = "로그인 실패";
        return;
    }

    const json = await res.json();
    accessToken = json.data.accessToken;

    loginStatus.textContent = "로그인 성공";
    loggedIn = true;

    userTab.disabled = false;
    adminTab.disabled = false;
    switchTab(true);
};

/*********************************
 * 탭 전환
 *********************************/
userTab.onclick = () => switchTab(true);
adminTab.onclick = () => switchTab(false);

function switchTab(isUser) {
    userPanel.classList.toggle("active", isUser);
    adminPanel.classList.toggle("active", !isUser);
}

/*********************************
 * WebSocket (JWT 포함)
 *********************************/
function connectWS(onConnect) {
    const protocol = location.protocol === "https:" ? "wss" : "ws";
    const socket = new WebSocket(`${protocol}://${location.host}/ws`);
    const client = Stomp.over(socket);
    client.debug = null;

    client.connect(
        {Authorization: `Bearer ${accessToken}`},
        onConnect
    );

    return client;
}

/*********************************
 * AI FAQ
 *********************************/
aiSendBtn.onclick = async () => {
    const q = aiInput.value;
    if (!q) return;

    add(aiMessages, "ME", q);
    aiInput.value = "";

    const res = await authFetch("/api/ai/faq", {
        method: "POST",
        headers: {"Content-Type": "text/plain"},
        body: q,
    });

    if (!res.ok) {
        add(aiMessages, "ERROR", "AI 호출 실패");
        return;
    }

    const json = await res.json();
    const data = json.data;

    add(aiMessages, "AI", data.answer);

    conversationId = data.conversationId;
    userConversationId.textContent = conversationId;

    if (data.handoverTriggered) {
        add(aiMessages, "SYSTEM", "상담사 대기중입니다.");
        userConnectBtn.disabled = false;
    }
};

/*********************************
 * 사용자 채팅
 *********************************/
userConnectBtn.onclick = () => {
    userStomp = connectWS(() => {
        userStomp.subscribe(`/sub/chat/${conversationId}`, (msg) => {
            const payload = JSON.parse(msg.body);

            if (payload.senderRole === "MEMBER") {
                add(userChatMessages, "ME", payload.message);
            } else {
                add(userChatMessages, "ADMIN", payload.message);
            }
        });

        userSendBtn.disabled = false;
        userStatus.textContent = "상담중";
    });
};

userSendBtn.onclick = () => {
    const msg = userMessageInput.value;
    if (!msg) return;

    userStomp.send(
        `/pub/chat/${conversationId}`,
        {},
        JSON.stringify({message: msg})
    );

    userMessageInput.value = "";
};

/*********************************
 * 관리자
 *********************************/
adminConnectBtn.onclick = () => {
    adminStomp = connectWS(loadWaiting);
};

async function loadWaiting() {
    const res = await authFetch("/api/admin/chat/rooms/waiting");
    if (!res.ok) return;

    const rooms = await res.json();
    waitingList.innerHTML = "";

    rooms.forEach((r) => {
        const div = document.createElement("div");
        div.textContent = r.conversationId;
        div.onclick = () => accept(r.conversationId);
        waitingList.appendChild(div);
    });
}

async function accept(cid) {
    await authFetch("/api/admin/chat/rooms/handover/accept", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(cid),
    });

    conversationId = cid;
    adminConversationId.textContent = cid;

    adminStomp.subscribe(`/sub/chat/${conversationId}`, (msg) => {
        const payload = JSON.parse(msg.body);

        if (payload.senderRole === "ADMIN") {
            add(adminChatMessages, "ME", payload.message);
        } else {
            add(adminChatMessages, "USER", payload.message);
        }
    });

    adminSendBtn.disabled = false;
}

adminSendBtn.onclick = () => {
    const msg = adminMessageInput.value;
    if (!msg) return;

    adminStomp.send(
        `/pub/chat/${conversationId}`,
        {},
        JSON.stringify({message: msg})
    );

    adminMessageInput.value = "";
};

/*********************************
 * util
 *********************************/
function add(el, who, msg) {
    const d = document.createElement("div");
    d.textContent = `[${who}] ${msg}`;
    el.appendChild(d);
    el.scrollTop = el.scrollHeight;
}
