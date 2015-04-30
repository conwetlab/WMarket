/**
 *  Copyright (c) 2015, CoNWeT Lab., Universidad Politécnica de Madrid
 *  Code licensed under BSD 3-Clause (https://github.com/conwetlab/WMarket/blob/master/LICENSE)
 */


var CustomError = function CustomError(errorType, errorMessage) {
    Error.call(this, errorMessage);
    this.name = errorType;
};

CustomError.prototype = Object.create(Error.prototype);
CustomError.prototype.constructor = CustomError;
