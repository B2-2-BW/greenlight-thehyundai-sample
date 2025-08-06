<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.net.URLEncoder" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>접속 대기 중 | Greenlight</title>
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
        .spinner {
            border: 5px solid #eee;
            border-top: 5px solid #333;
            border-radius: 50%;
            width: 48px;
            height: 48px;
            animation: spin 1s linear infinite;
            margin: 24px 0;
        }
        @keyframes spin {
            0% { transform: rotate(0deg);}
            100% { transform: rotate(360deg);}
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
<%
    String entryId = request.getParameter("entryId");
    String nextUrl = request.getParameter("nextUrl"); // 상품상세 URL
%>
<div class="container">
    <div class="spinner"></div>
    <h1>사용자가 많아 접속 대기중이에요</h1>

    <div class="position" id="position-panel">
        대기열 정보를 불러오는 중...
    </div>

    <div class="info">
        <p>잠시만 기다리시면 순서에 따라 자동 접속됩니다.</p>
        <p>새로고침하면 대기시간이 길어질 수 있어요</p>
    </div>

    <button class="button" onclick="window.location.href='<%= nextUrl %>'">이전으로 돌아가기</button>
</div>

<script>
    const entryId = "<%= entryId %>";
    const nextUrl = "<%= nextUrl %>";

    //const eventSource = new EventSource(`/sse/actionGroupId=${actionGroupId}&entryId=${entryId}`);
    const eventSource = new EventSource(`http://localhost:18080/waiting/sse?actionGroupId=6&entryId=1:0MAWT7V41WPGB`);

    eventSource.onmessage = function(event) {
        const data = JSON.parse(event.data);
        const panel = document.getElementById("position-panel");
        console.log("sse응답"+data);

        if (data === "ALLOWED") {
            window.location.href = nextUrl; // 자동 입장
        } else {
            panel.innerHTML = `
        현재 대기 순번: <b>${data.position}</b><br>
        예상 대기 시간: <b>${data.estimatedWaitTime}분</b>
      `;
        }
    };

    eventSource.onerror = function() {
        document.getElementById("position-panel").innerText = "연결 오류 발생. 잠시 후 다시 시도해주세요.";
    };
</script>
</body>
</html>
