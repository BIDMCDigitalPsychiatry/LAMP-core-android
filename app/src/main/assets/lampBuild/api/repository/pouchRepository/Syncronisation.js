"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __generator = (this && this.__generator) || function (thisArg, body) {
    var _ = { label: 0, sent: function() { if (t[0] & 1) throw t[1]; return t[1]; }, trys: [], ops: [] }, f, y, t, g;
    return g = { next: verb(0), "throw": verb(1), "return": verb(2) }, typeof Symbol === "function" && (g[Symbol.iterator] = function() { return this; }), g;
    function verb(n) { return function (v) { return step([n, v]); }; }
    function step(op) {
        if (f) throw new TypeError("Generator is already executing.");
        while (_) try {
            if (f = 1, y && (t = op[0] & 2 ? y["return"] : op[0] ? y["throw"] || ((t = y["return"]) && t.call(y), 0) : y.next) && !(t = t.call(y, op[1])).done) return t;
            if (y = 0, t) op = [op[0] & 2, t.value];
            switch (op[0]) {
                case 0: case 1: t = op; break;
                case 4: _.label++; return { value: op[1], done: false };
                case 5: _.label++; y = op[1]; op = [0]; continue;
                case 7: op = _.ops.pop(); _.trys.pop(); continue;
                default:
                    if (!(t = _.trys, t = t.length > 0 && t[t.length - 1]) && (op[0] === 6 || op[0] === 2)) { _ = 0; continue; }
                    if (op[0] === 3 && (!t || (op[1] > t[0] && op[1] < t[3]))) { _.label = op[1]; break; }
                    if (op[0] === 6 && _.label < t[1]) { _.label = t[1]; t = op; break; }
                    if (t && _.label < t[2]) { _.label = t[2]; _.ops.push(op); break; }
                    if (t[2]) _.ops.pop();
                    _.trys.pop(); continue;
            }
            op = body.call(thisArg, _);
        } catch (e) { op = [6, e]; y = 0; } finally { f = t = 0; }
        if (op[0] & 5) throw op[1]; return { value: op[0] ? op[1] : void 0, done: true };
    }
};
Object.defineProperty(exports, "__esModule", { value: true });
var app_1 = require("../../app");
var CDB = process.env.CDB;
var syncCompStatus = false;
//Replication/Sync event handling
var onSyncError = function () {
    // tslint:disable-next-line:no-console
    console.log("error");
};
var onSyncChange = function () {
    if (!syncCompStatus) {
        // tslint:disable-next-line:no-console
        console.log(".....wait for completion");
    }
};
var onSyncPaused = function () {
    if (!syncCompStatus) {
        // tslint:disable-next-line:no-console
        console.log("...wait for completion");
    }
};
// sync user data bidirectional
var biSync = function (db, userId, identifier) { return __awaiter(void 0, void 0, void 0, function () {
    var local_1, remote_1, opts_1, repl, error_1;
    return __generator(this, function (_a) {
        switch (_a.label) {
            case 0:
                _a.trys.push([0, 2, , 3]);
                local_1 = new app_1.PouchDB(db);
                remote_1 = new app_1.PouchDB(CDB + "/" + db);
                opts_1 = {};
                if (identifier === '_id') {
                    opts_1 = { live: true, retry: true, filter: 'userfilter/by_user', query_params: { _id: userId } };
                }
                else if (identifier === 'origin') {
                    opts_1 = { live: true, retry: true, filter: 'userfilter/by_user', query_params: { origin: userId } };
                }
                else if (identifier === '#parent') {
                    opts_1 = { live: true, retry: true, filter: 'userfilter/by_user', query_params: { parent: userId } };
                }
                return [4 /*yield*/, remote_1.replicate.to(local_1, {
                        filter: 'userfilter/by_user',
                        query_params: { parent: userId }
                    }).on('complete', function () {
                        // tslint:disable-next-line:no-console
                        console.log(db + " replication from and to local.");
                        if (db == 'activity_event' || db == 'sensor_event') {
                            exports.indexCreate(db);
                        }
                        local_1.sync(remote_1, opts_1)
                            .on('change', onSyncChange)
                            .on('paused', onSyncPaused)
                            .on('error', onSyncError);
                        if (db == 'sensor_event') {
                            // tslint:disable-next-line:no-console
                            console.log("Syncronisation Completed!");
                            syncCompStatus = true;
                        }
                    }).on('error', function () {
                        // tslint:disable-next-line:no-console
                        console.log("ERROR ON COMPLETE");
                    })];
            case 1:
                repl = _a.sent();
                return [2 /*return*/, repl];
            case 2:
                error_1 = _a.sent();
                // tslint:disable-next-line:no-console
                console.log(error_1);
                return [3 /*break*/, 3];
            case 3: return [2 /*return*/];
        }
    });
}); };
//sync user data from remote db
var replicateFrom = function (db) { return __awaiter(void 0, void 0, void 0, function () {
    var local, remote, repl, error_2;
    return __generator(this, function (_a) {
        switch (_a.label) {
            case 0:
                _a.trys.push([0, 2, , 3]);
                local = new app_1.PouchDB(db);
                remote = new app_1.PouchDB(CDB + "/" + db);
                return [4 /*yield*/, remote.replicate.to(local).on('complete', function () {
                        // tslint:disable-next-line:no-console
                        console.log(db + " replication to local, pls wait.");
                    }).on('change', onSyncChange)];
            case 1:
                repl = _a.sent();
                return [2 /*return*/, repl];
            case 2:
                error_2 = _a.sent();
                // tslint:disable-next-line:no-console
                console.log(error_2);
                return [3 /*break*/, 3];
            case 3: return [2 /*return*/];
        }
    });
}); };
//delete indexing for pouch db --not used
exports.indexDelete = function (dbAct) { return __awaiter(void 0, void 0, void 0, function () {
    var db, indexesResult, err_1;
    return __generator(this, function (_a) {
        switch (_a.label) {
            case 0:
                _a.trys.push([0, 4, , 5]);
                app_1.PouchDB.plugin(require('pouchdb-find'));
                return [4 /*yield*/, new app_1.PouchDB(dbAct)
                    // tslint:disable-next-line:no-console
                ];
            case 1:
                db = _a.sent();
                // tslint:disable-next-line:no-console
                return [4 /*yield*/, console.log(dbAct)];
            case 2:
                // tslint:disable-next-line:no-console
                _a.sent();
                return [4 /*yield*/, db.getIndexes()];
            case 3:
                indexesResult = _a.sent();
                // tslint:disable-next-line:no-console
                console.log(indexesResult);
                return [3 /*break*/, 5];
            case 4:
                err_1 = _a.sent();
                // tslint:disable-next-line:no-console
                console.log(err_1);
                return [3 /*break*/, 5];
            case 5: return [2 /*return*/];
        }
    });
}); };
//Create appropriate indexing for pouch db
exports.indexCreate = function (dbAct) { return __awaiter(void 0, void 0, void 0, function () {
    var db, err_2;
    return __generator(this, function (_a) {
        switch (_a.label) {
            case 0:
                app_1.PouchDB.plugin(require('pouchdb-find'));
                db = new app_1.PouchDB('sensor_event');
                _a.label = 1;
            case 1:
                _a.trys.push([1, 10, , 11]);
                if (!(dbAct === "activity_event")) return [3 /*break*/, 5];
                // tslint:disable-next-line:no-console
                console.log("activity_event indexed");
                db = new app_1.PouchDB(dbAct);
                return [4 /*yield*/, db.createIndex({
                        index: {
                            fields: ['activity']
                        }
                    })];
            case 2:
                _a.sent();
                return [4 /*yield*/, db.createIndex({
                        index: {
                            fields: ['#parent']
                        }
                    })];
            case 3:
                _a.sent();
                return [4 /*yield*/, db.createIndex({
                        index: {
                            fields: ['timestamp']
                        }
                    })];
            case 4:
                _a.sent();
                return [3 /*break*/, 9];
            case 5:
                // tslint:disable-next-line:no-console
                console.log("sensor_event indexed");
                db = new app_1.PouchDB('sensor_event');
                return [4 /*yield*/, db.createIndex({
                        index: {
                            fields: ['origin']
                        }
                    })];
            case 6:
                _a.sent();
                return [4 /*yield*/, db.createIndex({
                        index: {
                            fields: ['#parent']
                        }
                    })];
            case 7:
                _a.sent();
                return [4 /*yield*/, db.createIndex({
                        index: {
                            fields: ['timestamp']
                        }
                    })];
            case 8:
                _a.sent();
                _a.label = 9;
            case 9: return [3 /*break*/, 11];
            case 10:
                err_2 = _a.sent();
                // tslint:disable-next-line:no-console
                console.log(err_2);
                return [3 /*break*/, 11];
            case 11: return [2 /*return*/];
        }
    });
}); };
//Sync between database, while handling API
exports.sync = function (from, to) { return __awaiter(void 0, void 0, void 0, function () {
    var local, remote, repl, error_3;
    return __generator(this, function (_a) {
        switch (_a.label) {
            case 0:
                _a.trys.push([0, 2, , 3]);
                local = new app_1.PouchDB(from);
                remote = new app_1.PouchDB(CDB + "/" + to);
                return [4 /*yield*/, local.replicate.to(remote, {
                        live: true,
                        retry: true
                    }).on('complete', function () {
                        // tslint:disable-next-line:no-console
                        console.log("synced on api");
                    }).on('change', onSyncChange)];
            case 1:
                repl = _a.sent();
                // tslint:disable-next-line:no-console
                console.log(CDB + "/" + to);
                return [2 /*return*/, repl];
            case 2:
                error_3 = _a.sent();
                return [2 /*return*/, error_3];
            case 3: return [2 /*return*/];
        }
    });
}); };
//get user data from local db
exports.activityData = function (userId) { return __awaiter(void 0, void 0, void 0, function () {
    var status, db, result, error_4;
    return __generator(this, function (_a) {
        switch (_a.label) {
            case 0:
                status = false;
                _a.label = 1;
            case 1:
                _a.trys.push([1, 3, , 4]);
                db = new app_1.PouchDB('activity_event');
                return [4 /*yield*/, db.allDocs({
                        include_docs: true,
                        attachments: true
                    })];
            case 2:
                result = _a.sent();
                if (result.total_rows > 0) {
                    //tslint:disable-next-line:no-console    
                    console.log("There is local data");
                    status = true;
                }
                return [3 /*break*/, 4];
            case 3:
                error_4 = _a.sent();
                //tslint:disable-next-line:no-console    
                console.log(error_4);
                return [3 /*break*/, 4];
            case 4: return [2 /*return*/, status];
        }
    });
}); };
exports.dataSync = function (userId) { return __awaiter(void 0, void 0, void 0, function () {
    return __generator(this, function (_a) {
        //tslint:disable-next-line:no-console
        console.log("no_data");
        try {
            //tslint:disable-next-line:no-console
            console.log("starting syncronisation");
            replicateFrom('activity');
            replicateFrom('activity_spec');
            replicateFrom('migrator_link');
            replicateFrom('researcher');
            replicateFrom('sensor');
            replicateFrom('sensor_spec');
            replicateFrom('root');
            replicateFrom('study');
            replicateFrom('tags');
            biSync('activity_event', userId, "#parent");
            biSync('sensor_event', userId, "#parent");
            biSync('credential', userId, "origin");
            biSync('participant', userId, "_id");
        }
        catch (error) {
            //tslint:disable-next-line:no-console
            console.log(error);
        }
        return [2 /*return*/];
    });
}); };
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiU3luY3JvbmlzYXRpb24uanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi8uLi9zcmMvcmVwb3NpdG9yeS9wb3VjaFJlcG9zaXRvcnkvU3luY3JvbmlzYXRpb24udHMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6Ijs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7QUFBQSxpQ0FBbUM7QUFDbkMsSUFBTSxHQUFHLEdBQUcsT0FBTyxDQUFDLEdBQUcsQ0FBQyxHQUFHLENBQUM7QUFDNUIsSUFBSSxjQUFjLEdBQUMsS0FBSyxDQUFDO0FBQ3pCLGlDQUFpQztBQUNqQyxJQUFNLFdBQVcsR0FBRztJQUNsQixzQ0FBc0M7SUFDdEMsT0FBTyxDQUFDLEdBQUcsQ0FBQyxPQUFPLENBQUMsQ0FBQztBQUV2QixDQUFDLENBQUE7QUFDRCxJQUFNLFlBQVksR0FBRztJQUNuQixJQUFHLENBQUMsY0FBYyxFQUFDO1FBQ2pCLHNDQUFzQztRQUN0QyxPQUFPLENBQUMsR0FBRyxDQUFDLDBCQUEwQixDQUFDLENBQUM7S0FDdkM7QUFFTCxDQUFDLENBQUE7QUFDRCxJQUFNLFlBQVksR0FBRztJQUNuQixJQUFHLENBQUMsY0FBYyxFQUFDO1FBQ25CLHNDQUFzQztRQUN0QyxPQUFPLENBQUMsR0FBRyxDQUFDLHdCQUF3QixDQUFDLENBQUM7S0FDckM7QUFFSCxDQUFDLENBQUE7QUFFRCwrQkFBK0I7QUFDL0IsSUFBTSxNQUFNLEdBQUcsVUFBTyxFQUFVLEVBQUUsTUFBYyxFQUFFLFVBQWtCOzs7Ozs7Z0JBRTVELFVBQVEsSUFBSSxhQUFPLENBQUMsRUFBRSxDQUFDLENBQUM7Z0JBQ3hCLFdBQVMsSUFBSSxhQUFPLENBQUksR0FBRyxTQUFJLEVBQUksQ0FBQyxDQUFDO2dCQUNyQyxTQUFPLEVBQUUsQ0FBQztnQkFDZCxJQUFJLFVBQVUsS0FBSyxLQUFLLEVBQUU7b0JBQ3hCLE1BQUksR0FBRyxFQUFFLElBQUksRUFBRSxJQUFJLEVBQUUsS0FBSyxFQUFF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