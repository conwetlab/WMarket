<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${ viewName == 'FilterByCategory' }">
<script>
  app.view.currentCategory = {
    displayName: "${ category.displayName }",
    name: "${ category.name }"
  };
</script>
</c:if>
<script src="${ pageContext.request.contextPath }/resources/marketplace/js/views/offerings/list.js"></script>