<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script> app.view.storeName = "${ store.name }"; </script>
<c:if test="${ not empty review }">
<script>
  app.user.review = {
    id: "${ review.id }",
    score: "${ review.score}",
    comment: "${ review.comment }"
  };
</script>
</c:if>
<script src="${ pageContext.request.contextPath }/resources/marketplace/js/views/stores/detail.js"></script>
<c:choose>
<c:when test="${ viewName == 'offeringList' }">
  <script src="${ pageContext.request.contextPath }/resources/marketplace/js/views/stores/offering.list.js"></script>
</c:when>
<c:when test="${ viewName == 'descriptionList' }">
</c:when>
<c:otherwise>
  <script src="${ pageContext.request.contextPath }/resources/marketplace/js/views/stores/form.js"></script>
  <c:choose>
  <c:when test="${ not empty form_data }">
    <script>
      app.view.storeForm
      <c:forEach var="field" items="${ form_data }">
        .addInitialValue("${ field.key }", "${ field.value }")
      </c:forEach>
        .addErrorMessage("${ form_error.fieldName }", "${ form_error.fieldError }")
    </script>
  </c:when>
  <c:when test="${ not empty store }">
    <script>
      app.view.storeForm
        .addInitialValue("displayName", "${ store.displayName }")
        .addInitialValue("url", "${ store.url }")
        .addInitialValue("comment", "${ store.comment }")
    </script>
  </c:when>
  </c:choose>
</c:otherwise>
</c:choose>
<script src="${ pageContext.request.contextPath }/resources/marketplace/js/plugins/ratings.js"></script>