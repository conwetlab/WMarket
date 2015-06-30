<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="container" app-view="offering-list">
<c:choose>
<c:when test="${ viewName == 'GroupByCategory' }">
  <div class="row" app-group="category"></div>
</c:when>
<c:when test="${ viewName == 'FilterByCategory' }">
  <div class="container-flex" app-filter="category"></div>
</c:when>
</c:choose>
</div>