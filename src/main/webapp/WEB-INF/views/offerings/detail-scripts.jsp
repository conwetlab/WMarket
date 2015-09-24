<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script>
  app.view.storeName = "${ offering.describedIn.store.name }";
  app.view.descriptionName = "${ offering.describedIn.name }";
  app.view.offeringName = "${ offering.name }";
</script>
<c:if test="${ not empty review }">
<script>
  app.user.review = {
    id: "${ review.id }",
    score: "${ review.score}",
    comment: "${ review.comment }"
  };
</script>
</c:if>
<script src="${ pageContext.request.contextPath }/resources/marketplace/js/views/offerings/detail.js"></script>
<script src="${ pageContext.request.contextPath }/resources/marketplace/js/plugins/reviews.js"></script>