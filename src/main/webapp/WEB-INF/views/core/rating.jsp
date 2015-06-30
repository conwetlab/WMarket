<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>

<c:set var="prefix"><t:getAsString name="prefix" ignore="true"/></c:set>
<c:set var="selector"><t:getAsString name="selector" ignore="true"/></c:set>

<div class="rating"
  <c:if test="${ not empty selector }">
    data-target="${ selector }"
  </c:if>
  >
  <input type="radio" name="rating" value="5" id="${ prefix }star5">
  <label for="${ prefix }star5" class="star" title="Loved it"></label>
  <input type="radio" name="rating" value="4" id="${ prefix }star4">
  <label for="${ prefix }star4" class="star" title="Liked it"></label>
  <input type="radio" name="rating" value="3" id="${ prefix }star3">
  <label for="${ prefix }star3" class="star" title="It's ok"></label>
  <input type="radio" name="rating" value="2" id="${ prefix }star2">
  <label for="${ prefix }star2" class="star" title="Disliked it"></label>
  <input type="radio" name="rating" value="1" id="${ prefix }star1">
  <label for="${ prefix }star1" class="star" title="Hated it"></label>
</div>