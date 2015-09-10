<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>

<div class="panel panel-default">
  <div class="panel-heading text-center">
    <div class="image-thumbnail thumbnail-bordered thumbnail-lg">
      <img class="image" src="${ offering.imageUrl }">
      <span class="rating-value rating-value-lighter rating-overall">
        <span class="fa fa-star">${ offering.averageScore }</span>
      </span>
    </div>
    <h4 class="panel-title">${ offering.displayName }</h4>
    <div class="panel-subtitle offering-categories">
    <c:forEach var="category" items="${ offering.categories }">
      <span class="label label-success">${ category.displayName }</span>
    </c:forEach>
    </div>
    <t:insertTemplate template="/WEB-INF/views/core/rating.jsp">
      <t:putAttribute name="selector" value=".modal-rating" />
    </t:insertTemplate>
    <div class="offering-url">
      <a class="btn btn-primary" href="${ offering.acquisitionUrl }">
        <span class="btn-icon fa fa-shopping-cart"></span>
        <span class="btn-text">Acquire</span>
      </a>
    </div>
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
        <a href="${ pageContext.request.contextPath }/offerings/${ offering.describedIn.store.name }/${ offering.describedIn.name }/${ offering.name }">
          <span class="fa fa-newspaper-o"></span>
          <span class="hidden-sm">General</span>
        </a>
      </div>
      <div
        <c:choose>
          <c:when test="${ currentView == 'priceplans' }">
            class="tab active"
          </c:when>
          <c:otherwise>
            class="tab"
          </c:otherwise>
        </c:choose>
      >
        <a href="${ pageContext.request.contextPath }/offerings/${ offering.describedIn.store.name }/${ offering.describedIn.name }/${ offering.name }/priceplans">
          <span class="fa fa-credit-card"></span>
          <span class="hidden-sm">Price plans</span>
        </a>
      </div>
      <div
        <c:choose>
          <c:when test="${ currentView == 'services' }">
            class="tab active"
          </c:when>
          <c:otherwise>
            class="tab"
          </c:otherwise>
        </c:choose>
      >
        <a href="${ pageContext.request.contextPath }/offerings/${ offering.describedIn.store.name }/${ offering.describedIn.name }/${ offering.name }/services">
          <span class="fa fa-server"></span>
          <span class="hidden-sm">Services</span>
        </a>
      </div>
      <c:choose>
        <c:when test="${ not empty bookmark }">

          <div class="btn-toggle-bookmark tab tab-danger">
            <a href="javascript:app.view.toggleBookmark();">
              <span class="fa fa-bookmark"></span>
              <span class="hidden-sm">Remove bookmark</span>
            </a>
          </div>

        </c:when>
        <c:otherwise>

          <div class="btn-toggle-bookmark tab">
            <a href="javascript:app.view.toggleBookmark();">
              <span class="fa fa-bookmark"></span>
              <span class="hidden-sm">Add bookmark</span>
            </a>
          </div>

        </c:otherwise>
      </c:choose>
    </div>
  </div>
</div>

<t:insertTemplate template="/WEB-INF/views/core/rating-modal.jsp">
  <t:putAttribute name="title" value="Review for this offering" />
</t:insertTemplate>