<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>상품 상세</title>
    <meta charset="UTF-8">
</head>
<body>
<h2>${slitmNm}</h2>
<p>${slitmDesc}</p>
<form action="/order" method="post">
    <input type="hidden" name="slitmCd" value="${slitmCd}">
    <button type="submit">주문하기</button>
</form>
</body>
</html>