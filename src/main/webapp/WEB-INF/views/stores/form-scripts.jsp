<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="util" uri="http://fiware.org/functions" %>

<script src="${ pageContext.request.contextPath }/resources/marketplace/js/views/stores/form.js"></script>
<c:choose>
  <c:when test="${ not empty form_data }">
  <script>
    app.view.storeForm
    <c:forEach var="field" items="${ form_data }">
      .addInitialValue("${ field.key }", "${ util:escapeJS(field.value) }")
    </c:forEach>
      .addErrorMessage("${ form_error.fieldName }", "${ form_error.fieldError }")
  </script>
  </c:when>
  <c:when test="${ not empty store }">
  <script>
    app.view.storeForm
      .addInitialValue("displayName", "${ util:escapeJS(store.displayName) }")
      .addInitialValue("url", "${ util:escapeJS(store.url) }")
      .addInitialValue("comment", "${ util:escapeJS(store.comment) }")
  </script>
  </c:when>
</c:choose>