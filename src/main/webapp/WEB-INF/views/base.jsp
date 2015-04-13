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

        <c:choose>
          <c:when test="${ not empty user }">

            <div class="navbar-element pull-left">
              <button id="toggle-left-sidebar" class="btn btn-default" type="button">
                <span class="fa fa-bars"></span>
              </button>
            </div><!-- /.navbar-element -->

            <form class="navbar-element pull-left">
              <div class="form-field form-addon">
                <input id="search-field" class="form-control" type="text" placeholder="TODO: Not implemented yet.">
                <button id="search" class="btn btn-default btn-addon" type="submit">
                  <span class="fa fa-search"></span>
                </button>
              </div>
            </form><!-- /.navbar-element -->

            <div class="navbar-element pull-right">
              <button id="toggle-right-sidebar" class="btn btn-primary-lighter" type="button">
                <span class="fa fa-user"></span>
                <span class="text-plain text-truncate hidden-smartphone">${ user.displayName }</span>
              </button>
            </div><!-- /.navbar-element -->

          </c:when>
          <c:when test="${ current_view == 'register_user' }">

            <div class="navbar-element pull-right">
              <a class="btn btn-primary-lighter" href="${ pageContext.request.contextPath }/login">
                <span class="text-plain">Sign In</span>
              </a>
            </div><!-- /.navbar-element -->

          </c:when>
          <c:otherwise>

            <div class="navbar-element pull-right">
              <a class="btn btn-warning" href="${ pageContext.request.contextPath }/register">
                <span class="text-plain">Sign Up</span>
              </a>
            </div><!-- /.navbar-element -->

          </c:otherwise>
        </c:choose>

      </div>
    </div><!-- /.navbar -->

    <c:choose>
      <c:when test="${ not empty user }">

        <form name="logout_form" method="post" action="${ pageContext.request.contextPath }/logout">
          <!-- <input name="${ _csrf.parameterName }" type="hidden" value="${ _csrf.token }" /> -->
        </form>

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
        <div class="list-group">
          <a class="list-group-item" href="${ pageContext.request.contextPath }/account">
                <span class="fa fa-cog fa-fw"></span>
                <span class="text-plain">Settings</span>
              </a>
              <a class="list-group-item" href="javascript:logout()">
                <span class="fa fa-sign-out fa-fw"></span>
                <span class="text-plain">Log out</span>
              </a>
            </div>
          </div>
        </div><!-- /.panel -->

      <div id="left-sidebar" class="panel panel-default panel-sliding panel-sliding-left">
        <div class="panel-body">

          <div class="list-group">
            <div class="list-group-heading">DESCRIPTIONS</div>
            <div class="list-group-body">
              <a class="list-group-item" href="${ pageContext.request.contextPath }/descriptions">
                <span class="fa fa-archive fa-fw"></span>
                <span class="text-plain">My descriptions</span>
              </a>
              <a class="list-group-item" href="${ pageContext.request.contextPath }/descriptions/register">
                <span class="fa fa-upload fa-fw"></span>
                <span class="text-plain">Upload a description</span>
              </a>
            </div>
          </div><!-- .list-group -->

          <div class="list-group">
            <div class="list-group-heading">STORES</div>
            <div id="store-list" class="list-group-body"></div>
            <div class="list-group-body">
              <a class="list-group-item" href="${ pageContext.request.contextPath }/stores/register">
                <span class="fa fa-plus-circle fa-fw"></span>
                <span class="text-plain">Register a store</span>
              </a>
            </div>
          </div><!-- .list-group -->

        </div><!-- /.panel-body -->
      </div><!-- /.panel -->

      </c:when>
    </c:choose>

    <c:if test="${ not empty message }">
      <div class="alert-dismissible alert-manager">
        <div class="alert alert-success">
          <span class="fa fa-check-circle"></span> ${ message }
        </div>
      </div>
    </c:if>

    <t:insertAttribute name="content" />

    <div class="footer container-fluid">
      <div class="vertical-divider"></div>
      <div class="footer-col text-left">
        <span class="text-plain text-default">© 2015 CoNWeT Lab., Universidad Politécnica de Madrid</span>
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

      $('.alert-dismissible > .alert').delay(2000).slideUp(500);

    </script>

    <c:choose>
      <c:when test="${ not empty user }">

        <script>

          var WMarket = {
            core: {},
            layout: {},
            resources: {}
          };

          WMarket.core.contextPath = "${ pageContext.request.contextPath }";

          WMarket.layout.btnSearch = $('#search');
          WMarket.layout.btnSearch.attr('disabled', true);

          WMarket.layout.fieldSearch = $('#search-field');
          WMarket.layout.fieldSearch.attr('disabled', true);

        </script>
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

          WMarket.layout.toggleFilters = $('#toggle-left-sidebar');
          WMarket.layout.menuFilters = $('#left-sidebar');

          WMarket.layout.toggleFilters.on('click', function (event) {
            event.preventDefault();

            if (this.classList.contains('active')) {
              this.classList.remove('active');
              WMarket.layout.menuFilters.removeClass('active');
            } else {
              this.classList.add('active');
              WMarket.layout.menuFilters.addClass('active');
            }

            event.stopPropagation();
          });

          var logout = function logout() {
            document.logout_form.submit();
          };
        </script>
        <script src="${ pageContext.request.contextPath }/resources/marketplace/js/CustomError.js"></script>
        <script src="${ pageContext.request.contextPath }/resources/marketplace/js/AlertManager.js"></script>
        <script src="${ pageContext.request.contextPath }/resources/marketplace/js/EndpointManager.js"></script>
        <script src="${ pageContext.request.contextPath }/resources/marketplace/js/Store.js"></script>
        <script src="${ pageContext.request.contextPath }/resources/marketplace/js/StoreManager.js"></script>
        <script src="${ pageContext.request.contextPath }/resources/marketplace/js/Offering.js"></script>
        <script>

          WMarket.layout.storeList = $('#store-list');

          WMarket.requests.attach('core', 'read', {
            namespace: "stores:collection",
            containment: WMarket.layout.storeList,
            alert: WMarket.alerts.warning("No web store available."),
            onSuccess: function (collection, containment) {
              var i, store;

              for (i = 0; i < collection.length; i++) {
                store = WMarket.resources.stores.addStore(collection[i]);
                containment.append(store.addClass('list-group-item').get());
              }
            },
            onFailure: function () {
              // TODO: code that identify what error was occurred.
            }
          });

        </script>

      </c:when>
    </c:choose>

    <t:insertAttribute name="extra-scripts" ignore="true" />
    <c:choose>
      <c:when test="${ not empty user }">

        <script>

          WMarket.requests.dispatch('core');

        </script>

      </c:when>
    </c:choose>

  </body>
</html>
