<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>

<div class="container">
  <div class="col-sm-10 col-md-4 col-lg-3">
    <t:insertTemplate template="/WEB-INF/views/users/header.jsp" />
  </div>
  <div class="col-sm-10 col-md-6 col-lg-5 col-lg-offset-1">
    <c:choose>
      <c:when test="${ user.oauth2 eq true }">

      <div class="panel panel-default">
        <div class="panel-heading text-center">
          <span class="panel-title">Personal Information</span>
        </div>
        <div class="panel-body">
          <div class="dl-group">
            <dl>
              <dt>Username</dt>
              <dd>${ user.userName }</dd>
            </dl>
            <dl>
              <dt>Full name</dt>
              <dd>${ user.displayName }</dd>
            </dl>
            <dl>
              <dt>Email</dt>
              <dd>${ user.email }</dd>
            </dl>
            <dl>
              <dt>OAuth2</dt>
              <dd>Logged in through 'Identity Manager'</dd>
            </dl>
          </div>
        </div>
      </div>

      </c:when>
      <c:otherwise>

      <div class="panel panel-default">
        <div class="panel-heading text-center">
          <span class="panel-title">Personal Information</span>
        </div>
        <div class="panel-body">
          <form class="col-md-8 col-md-offset-1" name="account_update_form" method="post" action="${ pageContext.request.contextPath }/account">
            <div class="form-options">
              <button type="submit" class="btn btn-success">
                <span class="btn-text">Save changes</span>
              </button>
            </div>
          </form>
        </div>
      </div>
      
      <c:if test="${ user.oauth2 eq false }">
        <div class="panel panel-default">
          <div class="panel-heading text-center">
            <span class="panel-title">Provider</span>
          </div>
          <div class="panel-body">
          	<div class="col-md-8 col-md-offset-1">
              <p class="text-justify">
              When you are registered as provider, the user interface will show you the options to create Stores and Descriptions.
              These extra functions are only useful when you are an offering provider. Otherwise, we recommend you to keep your
              consumer role.
              </p>
              <p class="text-justify">
                <strong>Current Status: </strong> You are registered as <strong>
                <c:choose>
                  <c:when test="${ user.provider }">
                  	provider
                  </c:when>
                  <c:otherwise>
                  	consumer
                  </c:otherwise>
                </c:choose>
                </strong>
              </p>
              <div class="form-options">
              <form method="post" action="${ pageContext.request.contextPath }/account/provider">
                <button type="submit" 
                  <c:choose>
                    <c:when test="${ user.provider }">
                      class="btn btn-danger"
                    </c:when>
                    <c:otherwise>
                      class="btn btn-success"
                    </c:otherwise>
                  </c:choose>
                >
                  <span class="btn-text btn-text-not-truncated">                  
                  	<c:choose>
                  	  <c:when test="${ user.provider }">
                  		I don't want to be a provider anymore
                  	  </c:when>
                  	  <c:otherwise>
                  	  	I want to to become a provider
                  	  </c:otherwise>
                  	</c:choose>
                  </span>
                </button>
              </form>
              </div>
            </div>
          </div>
        </div>
      </c:if>
      

      </c:otherwise>
    </c:choose>

  </div>
</div>