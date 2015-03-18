<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="row container-fluid">
  <div class="col-sm-10 col-md-10 col-lg-10">
    <div class="panel panel-default">
      <div class="panel-heading">
        <span class="panel-title">${ store.displayName }</span>
      </div>
      <div class="row panel-body">
        <dl class="dl-vertical col-sm-10 col-md-5 visible-sm-margin">
          <dt>URL</dt>
          <dd>${ store.url }</dd>
        </dl>
        <dl class="dl-vertical col-sm-10 col-md-5">
          <dt>Creator</dt>
          <dd>${ store.creator.displayName }</dd>
          <dt>Registration Date</dt>
          <dd><fmt:formatDate pattern="yyyy-MM-dd" value="${ store.registrationDate }" /></dd>
        </dl>
      </div>
	</div>
  </div>
</div><!-- /.container-fluid -->

<div class="row container-fluid">
  <div id="search-results" class="container-flex"></div>
</div><!-- /.container-fluid -->
