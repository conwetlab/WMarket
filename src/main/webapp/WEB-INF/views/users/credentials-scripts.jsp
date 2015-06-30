<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script src="${ pageContext.request.contextPath }/resources/marketplace/js/views/users/detail.js"></script>
<script src="${ pageContext.request.contextPath }/resources/marketplace/js/views/users/credentials.js"></script>
<c:if test="${ not empty form_error }">
<script>
    app.view.passwordUpdateForm
      .addErrorMessage("${ form_error.fieldName }", "${ form_error.fieldError }")
</script>
</c:if>