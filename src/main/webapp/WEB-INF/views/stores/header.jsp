<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>

<c:set var="isOwner" value="${ store.creator.userName == user.userName }"></c:set>

<div class="panel panel-default">
  <div class="panel-heading text-center">
    <span class="image-thumbnail thumbnail-bordered thumbnail-lg">
      <c:choose>
      <c:when test="${ not empty store.imagePath }">

      <img class="image" src="${ pageContext.request.contextPath }/${ store.imagePath }">

      </c:when>
      <c:otherwise>

      <span class="image image-avatar image-default-darker">
        <span class="fa fa-building fa-inverse"></span>
      </span>

      </c:otherwise>
      </c:choose>
      <span class="rating-value rating-value-lg rating-overall">
        <span class="fa fa-star">${ store.averageScore }</span>
      </span>
    </span>
    <span class="panel-title store-displayname">${ store.displayName }</span>
    <t:insertTemplate template="/WEB-INF/views/core/rating.jsp">
      <t:putAttribute name="selector" value=".modal-rating" />
    </t:insertTemplate>
  </div>
  <div class="panel-body">
    <div class="tab-group tab-group-vertical">

      <div
        <c:choose>
          <c:when test="${ viewName == 'offeringList' }">
            class="tab active"
          </c:when>
          <c:otherwise>
            class="tab"
          </c:otherwise>
        </c:choose>
      >
        <a href="${ pageContext.request.contextPath }/stores/${ store.name }/offerings">
          <span class="fa fa-cubes"></span>
          <span class="hidden-sm">All offerings</span>
        </a>
      </div><!-- /.tab -->

      <c:if test="${ user.provider }">
      <div
          <c:choose>
            <c:when test="${ viewName == 'descriptionList' }">
              class="tab active"
            </c:when>
            <c:otherwise>
              class="tab"
            </c:otherwise>
          </c:choose>
        >
          <a href="${ pageContext.request.contextPath }/stores/${ store.name }/descriptions">
            <span class="fa fa-archive"></span>
            <span class="hidden-sm">My descriptions</span>
          </a>
        </div><!-- /.tab -->
      </c:if>

      <div
        <c:choose>
          <c:when test="${ viewName == 'detail' }">
            class="tab active"
          </c:when>
          <c:otherwise>
            class="tab"
          </c:otherwise>
        </c:choose>
      >
        <a href="${ pageContext.request.contextPath }/stores/${ store.name }/about">
          <span class="fa fa-newspaper-o"></span>
          <span class="hidden-sm">About &amp; reviews</span>
        </a>
      </div><!-- /.tab -->

      <c:if test="${ isOwner }">

        <div class="tab tab-danger">
          <a class="delete-store">
            <span class="fa fa-trash"></span>
            <span class="hidden-sm">Delete store</span>
          </a>
        </div>

      </c:if>

    </div>
  </div>
</div>

<t:insertTemplate template="/WEB-INF/views/core/rating-modal.jsp">
  <t:putAttribute name="title" value="Review for this store" />
</t:insertTemplate>

<c:if test="${ isOwner }">

<div class="modal modal-delete">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-heading">
        <button type="button" class="btn-close" data-cancel>&times;</button>
        <span class="modal-title">Delete store</span>
      </div>
      <div class="modal-body">
        <form name="store_delete_form" method="post" action="${ pageContext.request.contextPath }/stores/${ store.name }/delete">
          <p class="text-justify">This operation cannot be undone. All the offerings and descriptions that are contained in this store will be deleted too. Please be certain.</p>
        </form>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-danger btn-delete" data-submit>Delete</button>
        <button type="button" class="btn btn-default" data-cancel>Close</button>
      </div>
    </div>
  </div>
</div>

</c:if>
