<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="util" uri="http://fiware.org/functions" %>

<script src="${ pageContext.request.contextPath }/resources/marketplace/js/views/descriptions/detail.js"></script>
<c:if test="${ viewName == 'detail' }">
<c:choose>
  <c:when test="${ not empty form_data }">
  <script>
    app.view.descriptionUpdateForm
    <c:forEach var="field" items="${ form_data }">
      .addInitialValue("${ field.key }", "${ util:escapeJS(field.value) }")
    </c:forEach>
      .addErrorMessage("${ form_error.fieldName }", "${ form_error.fieldError }")
  </script>
  </c:when>
  <c:otherwise>
  <script>
    app.view.descriptionUpdateForm
      .addInitialValue("displayName", "${ util:escapeJS(description.displayName) }")
      .addInitialValue("url", "${ util:escapeJS(description.url) }")
      .addInitialValue("comment", "${ util:escapeJS(util:unescapeHTML(description.comment)) }")
  </script>
  </c:otherwise>
</c:choose>
</c:if>