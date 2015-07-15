<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>

<div class="panel panel-default-darker">
  <div class="panel-heading text-center">
    <span class="image-thumbnail thumbnail-circle">
      <img class="image" src="${ user.imageUrl }" />
    </span>
    <span class="panel-title">${ user.displayName }</span>
  </div>
  <div class="panel-body">
    <div class="tab-group tab-group-vertical">
      <div
        <c:choose>
          <c:when test="${ currentView == 'detail' }">
            class="tab active"
          </c:when>
          <c:otherwise>
            class="tab"
          </c:otherwise>
        </c:choose>
      >
        <a href="${ pageContext.request.contextPath }/account">
          <span class="fa fa-newspaper-o"></span>
          <span class="hidden-sm">Personal</span>
        </a>
      </div>
      <c:if test="${ user.oauth2 eq false }">

      <div
        <c:choose>
          <c:when test="${ currentView == 'credentials' }">
            class="tab active"
          </c:when>
          <c:otherwise>
            class="tab"
          </c:otherwise>
        </c:choose>
      >
        <a href="${ pageContext.request.contextPath }/account/password">
          <span class="fa fa-key"></span>
          <span class="hidden-sm">Credentials</span>
        </a>
      </div>
      <div class="tab tab-danger">
        <a class="delete-account">
          <span class="fa fa-trash"></span>
          <span class="hidden-sm">Delete account</span>
        </a>
      </div>

      </c:if>
    </div>
  </div>
</div>

<c:if test="${ user.oauth2 eq false }">

<div class="modal modal-delete">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-heading">
        <button type="button" class="btn-close" data-cancel>&times;</button>
        <span class="modal-title">Delete account</span>
      </div>
      <div class="modal-body">
        <form name="account_delete_form" method="post" action="${ pageContext.request.contextPath }/account/delete">
          <p class="text-justify">This operation cannot be undone. All your stores, descriptions and offerings will be deleted too. Please be certain.</p>
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