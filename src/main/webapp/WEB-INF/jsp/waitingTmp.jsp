<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.net.URLEncoder" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/waitingPage.css">
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>접속 대기 중 | Greenlight</title>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/countup.js/2.6.2/countUp.umd.js"></script>

    <style>
        body {
            font-family: sans-serif;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100dvh;
            margin: 0;
        }
        .container {
            width: 90%;
            max-width: 320px;
            display: flex;
            flex-direction: column;
            align-items: center;
        }
        .info { text-align: center; margin: 16px 0; }
        .info p { margin: 4px 0; color: #666; }
        .position {
            margin: 24px 0;
            font-size: 18px;
        }
        .button {
            background: #fff;
            border: 1px solid #ccc;
            padding: 8px 16px;
            border-radius: 999px;
            cursor: pointer;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="spinner"></div>
    <h1>사용자가 많아 접속 대기중이에요</h1>

    <div id="position-panel" style="font-size: 24px; color: #375A4E; font-weight: bold;">
        대기열 정보를 불러오는 중...
    </div>

    <div class="info">
        <p>잠시만 기다리시면 순서에 따라 자동 접속됩니다.</p>
        <p>새로고침하면 대기시간이 길어질 수 있어요</p>
    </div>

<%--    <button class="button" onclick="window.location.href='<%= nextUrl %>'">이전으로 돌아가기</button>--%>
</div>

<!-- CDN으로 CountUp 로드 -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/countup.js/2.6.2/countUp.min.js"></script>
<script>
    document.cookie = `X-GREENLIGHT-TOKEN=eyJhbGciOiJIUzI1NiJ9.eyJhY3Rpb25Hcm91cElkIjo1LCJjdXN0b21lcklkIjoiMTowTUhaVEJHOE5INDJUIiwiYWN0aW9uSWQiOjEsImRlc3RpbmF0aW9uVXJsIjoiIiwidGltZXN0YW1wIjoxNzU0NDY1ODUxNDgwLCJzdWIiOiIxOjBNSFpUQkc4Tkg0MlQiLCJpYXQiOjE3NTQ0NjU4NTEsImV4cCI6MTc1NDU1MjI1MX0.JpZ8ItEZsmNOJDToNA79zLTkIlmZMMeYIZWEbo8B7cE; path=/`;

    // Step 1 ) 쿠키에서 토큰 읽기
    function getCookieValue(name) {
        const value = document.cookie
            .split('; ')
            .find(row => row.startsWith(name + '='))
            ?.split('=')[1];
        return value ? decodeURIComponent(value) : null;
    }

    // Step 2 ) JWT payload 디코딩
    function parseJwt(token) {
        try {
            const base64Url = token.split('.')[1];
            const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');

            // padding 추가
            const padded = base64.padEnd(base64.length + (4 - base64.length % 4) % 4, '=');

            const jsonPayload = atob(padded);
            return JSON.parse(jsonPayload);
        } catch (e) {
            console.error('JWT 파싱 실패:', e);
            return null;
        }
    }

    // 실행
    const token = getCookieValue('X-GREENLIGHT-TOKEN');
    const tokenData = parseJwt(token);

    console.log('[Greenlight] 토큰 데이터'+JSON.stringify(tokenData));

    if (!tokenData || !tokenData.customerId || !tokenData.actionId || !tokenData.destinationUrl) {
        alert('[Greenlight] 인증 정보가 올바르지 않습니다.');
    } else {
        const customerId = tokenData.customerId;
        const actionId = tokenData.actionId;
        const destinationUrl = tokenData.destinationUrl;

        const eventSource = new EventSource(`/waiting/sse?actionId=${actionId}&customerId=${customerId}`);

        eventSource.onmessage = function (event) {
            const data = JSON.parse(event.data);
            console.log('SSE 응답:', data);

            document.getElementById("position").innerText = data.position ?? "-";
            document.getElementById("waitTime").innerText = data.estimatedWaitTime ?? "-";

            if (data.waitStatus === "READY" && data.destinationUrl) {
                window.location.href = data.destinationUrl;
            }else {
            panel.innerHTML = `
                현재 대기 순번: <b>${data.position}</b><br>
                예상 대기 시간: <b>${data.estimatedWaitTime}분</b>
              `;
        }
        };

        eventSource.onerror = function (err) {
            document.getElementById("position-panel").innerText = "연결 오류 발생. 잠시 후 다시 시도해주세요.";
            console.error('SSE 연결 오류:', err);
            eventSource.close();
        };
    }

    // window.CountUp 로 사용해야 함
    const countUp = new window.CountUp('position-panel', 15, {
        duration: 1,
        separator: ',',
    });

    if (!countUp.error) {
        countUp.start();
    } else {
        console.error('CountUp 오류:', countUp.error);
    }

</script>
</body>
</html>
