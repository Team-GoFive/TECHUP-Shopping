/*********************************
 * 전역 상태
 *********************************/
let userStomp = null;
let adminStomp = null;
let conversationId = null;
let accessToken = null;

let userCursor = null;
let adminCursor = null;

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
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({question: q}),
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
 * 채팅 내역 로드 (REST)
 *********************************/
async function loadChatMessages(conversationId, container, cursor = null) {
    const params = new URLSearchParams();
    params.append("size", 20);
    if (cursor) params.append("cursor", cursor);

    const res = await authFetch(
        `/api/chat/${conversationId}/messages?` + params.toString()
    );

    if (!res.ok) return null;

    const json = await res.json();
    const page = json.data;
    console.log(page);
    const messages = page.list;

    console.log(messages);
    // 서버 DESC → 화면 ASC
    messages.reverse().forEach(m => {
        add(
            container,
            m.senderRole === "ADMIN" ? "ADMIN" : "USER",
            m.message
        );
    });

    return messages.length > 0 ? messages[0].createdAt : null;
}

/*********************************
 * 사용자 채팅
 *********************************/
userConnectBtn.onclick = async () => {
    if (!conversationId) return;

    await authFetch("/api/chat/apply", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({conversationId}),
    });

    userChatMessages.innerHTML = "";
    userCursor = await loadChatMessages(conversationId, userChatMessages);

    userStomp = connectWS(() => {
        userStomp.subscribe(`/sub/chat/${conversationId}`, (msg) => {
            const payload = JSON.parse(msg.body);
            add(
                userChatMessages,
                payload.senderRole,
                payload.message
            );
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

async function loadRooms(type) {
    let url = "/api/admin/chat/rooms";

    if (type === "waiting") url += "/waiting";
    if (type === "connected") url += "/connected";

    const res = await authFetch(url);
    if (!res.ok) return;

    const json = await res.json();
    const rooms = json.data;

    roomList.innerHTML = "";

    rooms.forEach(r => {
        const div = document.createElement("div");
        div.textContent = `[${r.status}] ${r.conversationId}`;
        div.onclick = () => openRoom(r.conversationId, r.status);
        roomList.appendChild(div);
    });
}

async function openRoom(cid, status) {
    conversationId = cid;
    adminConversationId.textContent = cid;
    adminChatMessages.innerHTML = "";

    // 대기중이면 accept 먼저
    if (status === "WAITING") {
        await authFetch("/api/admin/chat/rooms/handover/accept", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({conversationId: cid}),
        });
    }

    // 과거 메시지 로드
    adminCursor = await loadChatMessages(cid, adminChatMessages);

    // 기존 구독 있으면 해제
    if (adminStomp && adminStomp.subscription) {
        adminStomp.subscription.unsubscribe();
    }

    adminStomp.subscription =
        adminStomp.subscribe(`/sub/chat/${cid}`, (msg) => {
            const payload = JSON.parse(msg.body);
            add(
                adminChatMessages,
                payload.senderRole,
                payload.message
            );
        });

    adminSendBtn.disabled = false;
}

adminConnectBtn.onclick = () => {
    adminStomp = connectWS(() => {
        loadRooms("waiting"); // 기본은 대기중
    });
};

allRoomsBtn.onclick = () => loadRooms("all");
waitingRoomsBtn.onclick = () => loadRooms("waiting");
connectedRoomsBtn.onclick = () => loadRooms("connected");


async function loadWaiting() {
    const res = await authFetch("/api/admin/chat/rooms/waiting");
    const json = await res.json();
    const rooms = json.data;

    waitingList.innerHTML = "";
    rooms.forEach(r => {
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
    adminChatMessages.innerHTML = "";

    adminCursor = await loadChatMessages(conversationId, adminChatMessages);

    adminStomp.subscribe(`/sub/chat/${conversationId}`, (msg) => {
        const payload = JSON.parse(msg.body);
        add(
            adminChatMessages,
            payload.senderRole === "ADMIN" ? "ME" : "USER",
            payload.message
        );
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
