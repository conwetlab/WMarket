<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${ not empty current_description }">
  <script>
    location.hash = "#form_update_${ current_description }";
  </script>
</c:if>
<script src="${ pageContext.request.contextPath }/resources/marketplace/js/StoreDescriptionListView.js"></script>
