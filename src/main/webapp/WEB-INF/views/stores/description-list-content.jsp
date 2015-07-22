<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="container">
  <div class="col-sm-10 col-md-4 col-lg-3">
    <t:insertTemplate template="/WEB-INF/views/stores/header.jsp" />
  </div>
    <c:choose>
    <c:when test="${ not empty descriptions }">

    <div class="col-sm-10 col-md-6 col-lg-5 col-lg-offset-2">
      <t:insertTemplate template="/WEB-INF/views/descriptions/elements.jsp" />
    </div>

    </c:when>
    <c:otherwise>

    <div class="col-sm-10 col-md-6 col-lg-7">
      <div class="alert alert-warning">
        <span class="fa fa-exclamation-circle"></span> Sorry, no description available for this store.
      </div>
    </div>

    </c:otherwise>
    </c:choose>
</div>