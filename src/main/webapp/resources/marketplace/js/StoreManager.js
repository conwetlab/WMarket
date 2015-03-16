/**
 *  Copyright (c) 2015, CoNWeT Lab., Universidad Politécnica de Madrid
 *  Code licensed under BSD 3-Clause (https://github.com/conwetlab/WMarket/blob/master/LICENSE)
 */


var StoreManager = (function () {

	"use strict";

    var StoreManager = function StoreManager() {
        this.storeList = [];
    };

    StoreManager.prototype.addStore = function addStore(data) {
        var newStore;

        newStore = new Store(data);
        this.storeList.push(newStore);

        return newStore;
    };

    StoreManager.prototype.getStoreByName = function getStoreByName(name) {
        var found, i, storeFound;

        for (found = false, i = 0; !found && i < this.storeList.length; i++) {
            if (this.storeList[i].name == name) {
                found = true;
                storeFound = this.storeList[i];
            }
        }

        return storeFound;
    };

    if (typeof WMarket !== 'undefined' && 'resources' in WMarket) {
        WMarket.resources.stores = new StoreManager();
    }

    return StoreManager;

})();
