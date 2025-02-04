async function fetchWithAuth(url, options = {}) {
    let accessToken = getAccessToken();
    
    if (!accessToken) {
        console.log("🔴 Access Token이 없음. 로그인 필요");
        window.location.href = "/auth/login";
        return;
    }

    // ✅ 기존 요청에 Authorization 헤더 추가
    options.headers = {
        ...options.headers,
        "Authorization": `Bearer ${accessToken}`
    };

    let response = await fetch(url, {
        ...options,
        credentials: "include"  // ✅ 쿠키 포함 요청
    });

    // ✅ Access Token이 만료된 경우 (401 Unauthorized)
    if (response.status === 401) {
        console.log("🔴 Access Token이 만료됨. Refresh Token으로 재발급 요청");

        // ✅ Refresh Token을 이용해 Access Token 재발급 요청
        const refreshResponse = await fetch("/auth/refresh", {
            method: "POST",
            credentials: "include"
        });

        if (refreshResponse.ok) {
            const data = await refreshResponse.json();
            console.log("🟢 새로운 Access Token 발급 완료:", data.accessToken);

            // ✅ 새로운 Access Token을 저장
            setAccessToken(data.accessToken);

            // ✅ 원래 요청을 새로운 Access Token으로 다시 실행
            return fetch(url, {
                ...options,
                credentials: "include",
                headers: {
                    ...options.headers,
                    "Authorization": `Bearer ${data.accessToken}`
                }
            });
        } else {
            console.log("🔴 Refresh Token도 만료됨. 로그인 페이지로 이동");
            window.location.href = "/auth/login";
        }
    }

    return response;
}

// ✅ 로컬 스토리지에서 Access Token 가져오기
function getAccessToken() {
    return localStorage.getItem("accessToken");
}

// ✅ 새로운 Access Token을 로컬 스토리지에 저장
function setAccessToken(token) {
    localStorage.setItem("accessToken", token);
}

// ✅ 10초마다 자동으로 로그인 상태 확인 (Access Token 만료 감지)
setInterval(() => {
    fetchWithAuth("/auth/check").then(response => response.json()).then(data => {
        console.log("🔍 로그인 상태 자동 확인:", data);
        if (!data.loggedIn) {
            window.location.href = "/auth/login";
        }
    });
}, 10000);
