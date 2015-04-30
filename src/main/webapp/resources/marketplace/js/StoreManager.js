/**
 *  Copyright (c) 2015, CoNWeT Lab., Universidad Politécnica de Madrid
 *  Code licensed under BSD 3-Clause (https://github.com/conwetlab/WMarket/blob/master/LICENSE)
 */


var StoreManager = (function () {

    "use strict";

    var StoreManager = function StoreManager() {
        this.storeList = {};
    };

    StoreManager.prototype.addStore = function addStore(data) {
        var store;

        store = new Store(data);
        this.storeList[store.name] = store;

        return store;
    };

    StoreManager.prototype.getStoreByName = function getStoreByName(name) {
        var store, storeException;

        if (!(store=this.storeList[name])) {
            storeException = new Exception("The store '%(name)s' is not saved.".replace("%(name)s", name));
            storeException.name = "Store Not Found";

            throw storeException;
        }

        return store;
    };

    if (WMarket && WMarket.resources) {
        WMarket.resources.stores = new StoreManager();
    }

    return StoreManager;

})();
