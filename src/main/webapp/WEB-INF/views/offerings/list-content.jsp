<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="container" app-view="offering-list">
<c:choose>
<c:when test="${ viewName == 'GroupByCategory' }">
  <h2>Other users are looking at</h2>
  <div class="row" app-order="viewedByOthers"></div>
  <h2>Last viewed</h2>
  <div class="row" app-order="lastviewed"></div>
  <h2>Categories</h2>
  <div class="row" app-group="category"></div>
</c:when>
<c:when test="${ viewName == 'FilterByCategory' }">
  <div class="container-flex" app-filter="category"></div>
</c:when>
</c:choose>
</div>