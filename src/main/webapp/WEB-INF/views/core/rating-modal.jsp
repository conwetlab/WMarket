<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>

<div class="modal modal-rating">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-heading">
        <button type="button" class="btn-close" data-cancel>&times;</button>
        <span class="modal-title"><t:getAsString name="title" /></span>
      </div>
      <div class="modal-body">
        <form name="review_form">
          <div class="form-field">
            <label class="field-label">Rating</label>
            <t:insertTemplate template="/WEB-INF/views/core/rating.jsp">
              <t:putAttribute name="prefix" value="form_" />
            </t:insertTemplate>
          </div>
        </form>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-danger btn-delete" data-submit>Delete</button>
        <button type="button" class="btn btn-success btn-update" data-submit>Submit</button>
        <button type="button" class="btn btn-default" data-cancel>Close</button>
      </div>
    </div>
  </div>
</div>