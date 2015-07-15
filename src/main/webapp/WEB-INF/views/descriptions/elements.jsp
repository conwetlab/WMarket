<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:forEach var="description" items="${ descriptions }">
<div class="panel panel-default">
  <div class="panel-heading text-center">
    <span class="image-thumbnail thumbnail-circle">
      <span class="image image-avatar image-default-darker">
        <span class="fa fa-archive fa-inverse"></span>
      </span>
    </span>
    <a class="panel-title" href="${ pageContext.request.contextPath }/stores/${ description.store.name }/descriptions/${ description.name }">${ description.displayName }</a>
  </div>
  <div class="panel-body">
    <div class="dl-group">
      <dl>
        <dt>Store</dt>
        <dd>
          <a href="${ pageContext.request.contextPath }/stores/${ description.store.name }/offerings">${ description.store.displayName }</a>
        </dd>
      </dl>
      <dl>
        <dt>Registered at</dt>
        <dd class="date"><fmt:formatDate pattern="MMM dd, yyyy HH:mm:ss" value="${ description.createdAt }" /></dd>
      </dl>
    </div>
    <p class="text-bold">Included offerings</p>
    <div class="description-offerings">
      <div class="row-sliding" style="width: ${ fn:length(description.offerings) * 210 }px;">
      <c:forEach var="offering" items="${ description.offerings }">

        <div class="panel panel-default-lighter offering-item">
          <div class="panel-heading text-center">
            <a href="${ pageContext.request.contextPath }/offerings/${ description.store.name }/${ description.name }/${ offering.name }" class="image-thumbnail thumbnail-bordered">
              <img class="image offering-image" src="${ offering.imageUrl }" />
            </a>
            <a href="${ pageContext.request.contextPath }/offerings/${ description.store.name }/${ description.name }/${ offering.name }" class="panel-title text-truncate">${ offering.displayName }</a>
          </div>
          <div class="panel-body">
            <div class="offering-description">${ offering.description }</div>
          </div>
        </div>

      </c:forEach>
      </div>
    </div>
  </div>
</div>
</c:forEach>