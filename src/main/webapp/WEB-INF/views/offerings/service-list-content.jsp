<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>

<div class="container">
  <div class="col-sm-10 col-md-4 col-lg-3">
    <t:insertTemplate template="/WEB-INF/views/offerings/header.jsp" />
  </div>
  <div class="col-sm-10 col-md-6 col-lg-7">
    <div class="service-group">
      <c:choose>
      <c:when test="${ not empty offering.services }">
        <c:forEach var="service" items="${ offering.services }">
            <div class="service-item">
              <div class="service-name">
                <span>${ service.displayName }</span>
              </div>
              <div class="service-content">
                <div class="service-categories">
                <c:forEach var="category" items="${ service.categories }">
                  <span class="label label-success">${ category.displayName }</span>
                </c:forEach>
                </div>
                <div class="service-comment">${ service.comment }</div>
              </div>
            </div>
        </c:forEach>
      </c:when>
      <c:otherwise>

        <div class="alert alert-warning">
          <span class="fa fa-exclamation-circle"></span> Sorry, no service available for this offering.
        </div>

      </c:otherwise>
      </c:choose>
    </div>
  </div>
</div>