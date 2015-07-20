<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>Forbidden</title>
    <link rel="stylesheet" href="${ pageContext.request.contextPath }/resources/font-awesome/css/font-awesome.css">
    <link rel="stylesheet" href="${ pageContext.request.contextPath }/resources/marketplace/css/globals.css">
    <link rel="stylesheet" href="${ pageContext.request.contextPath }/resources/marketplace/css/grid.css">
    <link rel="stylesheet" href="${ pageContext.request.contextPath }/resources/marketplace/css/components/navbars.css">
    <link rel="stylesheet" href="${ pageContext.request.contextPath }/resources/marketplace/css/errormessages.css">
    <link rel="stylesheet" href="${ pageContext.request.contextPath }/resources/marketplace/css/helpers.css">
  </head>
  <body>
    <div class="navbar navbar-fixed-top">
      <div class="container">
        <div class="navbar-header">
          <a class="navbar-brand" href="${ pageContext.request.contextPath }">WMarket</a>
        </div>
      </div>
    </div>
    <div class="container text-center">
      <div class="error-heading">
        <span class="fa fa-university"></span>
        <span class="error-code">403</span>
      </div>
      <div class="error-body">You do not have permission to view the page you are trying to access.</div>
    </div>
    <div class="container">
      <div class="vertical-divider"></div>
      <div class="footer-col text-left">
        <span>© 2015 CoNWeT Lab., Universidad Politécnica de Madrid</span>
      </div>
      <div class="footer-col text-right">
        <a href="https://github.com/conwetlab/WMarket">GitHub</a> · <a href="http://catalogue.fiware.org/enablers/marketplace-wmarket">FIWARE</a>
      </div>
    </div>
  </body>
</html>