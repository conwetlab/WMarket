<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>${ title }</title>

    <link rel="stylesheet" href="${ pageContext.request.contextPath }/resources/font-awesome/css/font-awesome.css">
    <link rel="stylesheet" href="${ pageContext.request.contextPath }/resources/marketplace/css/marketplace-grid.css">
    <link rel="stylesheet" href="${ pageContext.request.contextPath }/resources/marketplace/css/marketplace-theme.css">
    <link rel="stylesheet" href="${ pageContext.request.contextPath }/resources/marketplace/css/marketplace-helpers.css">
  </head>
  <body>
    <div class="navbar navbar-fixed-top">
      <div class="container-fluid">

        <div class="navbar-header">
          <a class="navbar-brand" href="${ pageContext.request.contextPath }">WMarket</a>
        </div><!-- /.navbar-header -->

        <form class="navbar-element pull-left">
          <div class="form-field">
            <button id="toggle-left-sidebar" class="btn btn-default btn-addon" type="button">
              <span class="fa fa-bars"></span>
            </button>
            <input id="search-field" class="form-control" type="text" placeholder="TODO: Not implemented yet.">
            <button id="search" class="btn btn-default btn-addon" type="submit">
              <span class="fa fa-search"></span>
            </button>
          </div>
        </form><!-- /.navbar-element -->

        <c:choose>
          <c:when test="${ not empty user }">

            <div class="navbar-element pull-right">
              <button id="toggle-right-sidebar" class="btn btn-primary-lighter" type="submit">
                <span class="fa fa-user"></span>
                <span class="text-plain text-truncate hidden-smartphone">${ user.displayName }</span>
              </button>
            </div><!-- /.navbar-element -->

          </c:when>
        </c:choose>

      </div>
    </div><!-- /.navbar -->

    <c:choose>
      <c:when test="${ not empty user }">

        <div id="right-sidebar" class="panel panel-default-darker panel-sliding panel-sliding-right">
          <div class="panel-heading">
            <span class="fa-avatar fa-stack">
              <i class="fa fa-circle fa-stack-2x"></i>
              <i class="fa fa-user fa-stack-1x fa-inverse"></i>
            </span>
            <span class="panel-title text-truncate">${ user.displayName }</span>
            <span class="panel-subtitle text-truncate">${ user.email }</span>
          </div>
          <div class="panel-body">
            <div class="list">
              <div class="list-body">
                <a class="list-item" href="#">
                  <i class="fa fa-cog fa-fw"></i>&nbsp; Settings
                </a>
                <a class="list-item" href="#">
                  <i class="fa fa-sign-out fa-fw"></i>&nbsp; Sign out
                </a>
              </div>
            </div>
          </div>
        </div><!-- /.panel -->

      </c:when>
    </c:choose>

    <t:insertAttribute name="left-sidebar" ignore="true" />
    <t:insertAttribute name="content" />

    <div class="footer container-fluid">
      <div class="vertical-divider"></div>
      <div class="footer-col text-left">
        <span class="text-plain">© 2015 CoNWeT Lab., Universidad Politécnica de Madrid</span>
      </div>
      <div class="footer-col text-center">
        <button class="btn btn-default" type="button">
          <span class="fa fa-chevron-up"></span>
        </button>
      </div>
      <div class="footer-col text-right">
        <a href="https://github.com/conwetlab/WMarket">GitHub</a> · <a href="http://catalogue.fiware.org/enablers/marketplace-wmarket">FIWARE</a>
      </div>
    </div><!-- /.footer -->

    <script src="${ pageContext.request.contextPath }/resources/jquery/js/jquery-1.11.2.js"></script>
    <script>

      var WMarket = {
        core: {},
        layout: {}
      };

      WMarket.core.contextPath = "${ pageContext.request.contextPath }";

      WMarket.layout.toggleFilters = $('#toggle-left-sidebar');
      WMarket.layout.toggleFilters.attr('disabled', true);

      WMarket.layout.btnSearch = $('#search');
      WMarket.layout.btnSearch.attr('disabled', true);

      WMarket.layout.fieldSearch = $('#search-field');
      WMarket.layout.fieldSearch.attr('disabled', true);

    </script>

    <c:choose>
      <c:when test="${ not empty user }">

        <script>

          WMarket.layout.togglePrefs = $('#toggle-right-sidebar');
          WMarket.layout.menuPrefs   = $('#right-sidebar');

          WMarket.layout.togglePrefs.on('click', function (event) {
            event.preventDefault();

            if (this.classList.contains('active')) {
              this.classList.remove('active');
              WMarket.layout.menuPrefs.removeClass('active');
            } else {
              this.classList.add('active');
              WMarket.layout.menuPrefs.addClass('active');
            }

            event.stopPropagation();
          });

        </script>

      </c:when>
    </c:choose>

    <script src="${ pageContext.request.contextPath }/resources/marketplace/js/AlertManager.js"></script>
    <script src="${ pageContext.request.contextPath }/resources/marketplace/js/EndpointManager.js"></script>
    <script src="${ pageContext.request.contextPath }/resources/marketplace/js/Store.js"></script>
    <t:insertAttribute name="extra-scripts" ignore="true" />
  </body>
</html>
