<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>

<c:set var="isStoreOwner" value="${ store.creator.userName == user.userName }"></c:set>

<c:if test="${ isStoreOwner }">

  <form name="store_delete_form" method="post" action="${ pageContext.request.contextPath }/stores/${ store.name }/delete">
    <!-- <p class="text-justify">This operation cannot be undone. All the offerings and descriptions that are contained in this store will be deleted too. Please be certain.</p> -->
  </form>

  <script>
    var deleteStore = function deleteStore() {
      document.store_delete_form.submit();
    };
  </script>

</c:if>

<div class="panel panel-default">

  <div class="panel-heading">

    <c:if test="${ not empty store.imagePath }">
      <span class="image-thumbnail image-thumbnail-lg">
        <img class="image image-circle" src="${ pageContext.request.contextPath }/${ store.imagePath }">
      </span>
    </c:if>

    <span class="panel-title">${ store.displayName }</span>
  </div><!-- /.panel-heading -->

  <div class="panel-body">
    <div class="tab-group tab-group-vertical">

      <div
        <c:choose>
          <c:when test="${ currentStoreView == 'offeringList' }">
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

      <div
        <c:choose>
          <c:when test="${ currentStoreView == 'descriptionList' }">
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

      <div
        <c:choose>
          <c:when test="${ currentStoreView == 'detail' }">
            class="tab active"
          </c:when>
          <c:otherwise>
            class="tab"
          </c:otherwise>
        </c:choose>
      >
        <a href="${ pageContext.request.contextPath }/stores/${ store.name }/about">
          <span class="fa fa-newspaper-o"></span>
          <span class="hidden-sm">About</span>
        </a>
      </div><!-- /.tab -->

      <c:if test="${ isStoreOwner }">

        <div class="tab tab-danger">
          <a href="javascript:deleteStore()">
            <span class="fa fa-trash"></span>
            <span class="hidden-sm">Delete store</span>
          </a>
        </div><!-- /.tab -->

      </c:if>

    </div>
  </div>
</div>
