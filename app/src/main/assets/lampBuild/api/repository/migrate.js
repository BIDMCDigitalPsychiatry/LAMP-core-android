"use strict";
var __makeTemplateObject = (this && this.__makeTemplateObject) || function (cooked, raw) {
    if (Object.defineProperty) { Object.defineProperty(cooked, "raw", { value: raw }); } else { cooked.raw = raw; }
    return cooked;
};
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
var __read = (this && this.__read) || function (o, n) {
    var m = typeof Symbol === "function" && o[Symbol.iterator];
    if (!m) return o;
    var i = m.call(o), r, ar = [], e;
    try {
        while ((n === void 0 || n-- > 0) && !(r = i.next()).done) ar.push(r.value);
    }
    catch (error) { e = { error: error }; }
    finally {
        try {
            if (r && !r.done && (m = i["return"])) m.call(i);
        }
        finally { if (e) throw e.error; }
    }
    return ar;
};
var __spread = (this && this.__spread) || function () {
    for (var ar = [], i = 0; i < arguments.length; i++) ar = ar.concat(__read(arguments[i]));
    return ar;
};
var __values = (this && this.__values) || function(o) {
    var s = typeof Symbol === "function" && Symbol.iterator, m = s && o[s], i = 0;
    if (m) return m.call(o);
    if (o && typeof o.length === "number") return {
        next: function () {
            if (o && i >= o.length) o = void 0;
            return { value: o && o[i++], done: !o };
        }
    };
    throw new TypeError(s ? "Object is not iterable." : "Symbol.iterator is not defined.");
};
Object.defineProperty(exports, "__esModule", { value: true });
var app_1 = require("../app");
var SensorEvent_1 = require("../model/SensorEvent");
var ActivityEvent_1 = require("../model/ActivityEvent");
var ActivityRepository_1 = require("../repository/ActivityRepository");
var nanoid_1 = require("nanoid");
var uuid = nanoid_1.customAlphabet("1234567890abcdefghjkmnpqrstvwxyz", 20); // crockford-32
// TODO: Switch to _local/ documents!
// IMPORTANT!
// This isn't synchronized/atomic yet.
// Maybe this needs to be a job queue instead.
var _migrator_active = false;
Object.defineProperty(Number, "parse", {
    value: function (input) {
        if (input === null || input === undefined)
            return undefined;
        if (typeof input === "number")
            return input;
        return isNaN(Number(input)) ? undefined : Number(input);
    },
});
///
///
///
function _migrate_sensor_event() {
    var _a;
    return __awaiter(this, void 0, void 0, function () {
        var MigratorLink, change_tracking, change_version, next_version, _b, _c, result1, i, out, result2, i, out, e_1;
        return __generator(this, function (_d) {
            switch (_d.label) {
                case 0:
                    if (_migrator_active) {
                        return [2 /*return*/, console.info("sensor_event migration aborted due to existing migrator activity")];
                    }
                    _migrator_active = true;
                    _d.label = 1;
                case 1:
                    _d.trys.push([1, 15, 16, 17]);
                    MigratorLink = app_1.Database.use("root");
                    return [4 /*yield*/, MigratorLink.get("#change_tracking")];
                case 2:
                    change_tracking = (_d.sent());
                    change_version = change_tracking.db.sensor_event;
                    if (change_version === undefined) {
                        return [2 /*return*/, console.info("sensor_event migration aborted due to missing change_version token")];
                    }
                    _c = (_b = Number).parse;
                    return [4 /*yield*/, app_1.SQL.query(templateObject_1 || (templateObject_1 = __makeTemplateObject(["SELECT CHANGE_TRACKING_CURRENT_VERSION()"], ["SELECT CHANGE_TRACKING_CURRENT_VERSION()"])))];
                case 3:
                    next_version = (_a = _c.apply(_b, [(_d.sent()).recordset[0][""]]), (_a !== null && _a !== void 0 ? _a : 0));
                    change_tracking.db.sensor_event = next_version;
                    if (next_version - change_version === 0) {
                        return [2 /*return*/, console.info("sensor_event migration aborted due to no tracked changes")];
                    }
                    console.dir("sensor_event migration from " + (change_version !== null && change_version !== void 0 ? change_version : 0) + " to " + (next_version !== null && next_version !== void 0 ? next_version : 0));
                    return [4 /*yield*/, app_1.SQL.request().query("\n        SELECT \n            timestamp, \n            type, \n            data, \n            X.StudyId AS [#parent]\n        FROM (\n            SELECT\n                Users.AdminID, \n                Users.StudyId, \n                Users.IsDeleted,\n                DATEDIFF_BIG(MS, '1970-01-01', U.CreatedOn) AS timestamp, \n                U.type,\n                U.data\n            FROM HealthKit_DailyValues\n            UNPIVOT (data FOR type IN (\n                Height, Weight, HeartRate, BloodPressure, \n                RespiratoryRate, Sleep, Steps, FlightClimbed, \n                Segment, Distance\n            )) U\n            LEFT JOIN Users\n                ON U.UserID = Users.UserID\n            WHERE U.data != ''\n                AND HKDailyValueID IN (\n                    SELECT C.HKDailyValueID\n                    FROM CHANGETABLE(CHANGES HealthKit_DailyValues, " + (change_version !== null && change_version !== void 0 ? change_version : "NULL") + ") AS C \n                    WHERE SYS_CHANGE_CONTEXT IS NULL\n                )\n            UNION ALL \n            SELECT\n                Users.AdminID, \n                Users.StudyId, \n                Users.IsDeleted,\n                DATEDIFF_BIG(MS, '1970-01-01', DateTime) AS timestamp,\n                REPLACE(HKParamName, ' ', '') AS type,\n                Value AS data\n            FROM HealthKit_ParamValues\n            LEFT JOIN Users\n                ON HealthKit_ParamValues.UserID = Users.UserID\n            LEFT JOIN HealthKit_Parameters\n                ON HealthKit_Parameters.HKParamID = HealthKit_ParamValues.HKParamID\n            WHERE HKParamValueID IN (\n                SELECT C.HKParamValueID\n                FROM CHANGETABLE(CHANGES HealthKit_ParamValues, " + (change_version !== null && change_version !== void 0 ? change_version : "NULL") + ") AS C \n                WHERE SYS_CHANGE_CONTEXT IS NULL\n            )\n        ) X\n\t;")];
                case 4:
                    result1 = (_d.sent()).recordset.map(function (raw) {
                        var _a, _b;
                        var obj = new SensorEvent_1.SensorEvent();
                        obj["#parent"] = (_a = app_1.Decrypt(raw["#parent"])) === null || _a === void 0 ? void 0 : _a.replace(/^G/, "");
                        obj.timestamp = (_b = Number.parse(raw.timestamp), (_b !== null && _b !== void 0 ? _b : 0));
                        obj.sensor = Object.entries(HK_LAMP_map).filter(function (x) { return x[1] === raw.type; })[0][0];
                        obj.data = (HK_to_LAMP[obj.sensor] || (function (x) { return x; }))(raw.data);
                        return obj;
                    });
                    console.dir("[HealthKit_Values] migrating " + result1.length + " events");
                    i = 0;
                    _d.label = 5;
                case 5:
                    if (!(i < Math.ceil(result1.length / 30000))) return [3 /*break*/, 8];
                    return [4 /*yield*/, app_1.Database.use("sensor_event").bulk({ docs: result1.slice(i * 30000, (i + 1) * 30000) })];
                case 6:
                    out = _d.sent();
                    console.dir(out.filter(function (x) { return !!x.error; }));
                    _d.label = 7;
                case 7:
                    i++;
                    return [3 /*break*/, 5];
                case 8: return [4 /*yield*/, app_1.SQL.request().query("\n\tSELECT \n        DATEDIFF_BIG(MS, '1970-01-01', Locations.CreatedOn) AS timestamp,\n        Latitude AS lat,\n        Longitude AS long,\n        LocationName AS location_name,\n        Users.StudyId AS [#parent]\n    FROM Locations\n    LEFT JOIN Users\n        ON Locations.UserID = Users.UserID\n    WHERE LocationID IN (\n        SELECT C.LocationID\n        FROM CHANGETABLE(CHANGES Locations, " + (change_version !== null && change_version !== void 0 ? change_version : "NULL") + ") AS C \n        WHERE SYS_CHANGE_CONTEXT IS NULL\n    )\n    ;")];
                case 9:
                    result2 = (_d.sent()).recordset.map(function (raw) {
                        var _a, _b;
                        var x = toLAMP(raw.location_name);
                        var obj = new SensorEvent_1.SensorEvent();
                        obj["#parent"] = (_a = app_1.Decrypt(raw["#parent"])) === null || _a === void 0 ? void 0 : _a.replace(/^G/, "");
                        obj.timestamp = (_b = Number.parse(raw.timestamp), (_b !== null && _b !== void 0 ? _b : 0));
                        obj.sensor = SensorEvent_1.SensorName.ContextualLocation;
                        obj.data = {
                            latitude: parseFloat(app_1.Decrypt(raw.lat) || raw.lat),
                            longitude: parseFloat(app_1.Decrypt(raw.long) || raw.long),
                            accuracy: -1,
                            context: {
                                environment: x[0] || null,
                                social: x[1] || null,
                            },
                        };
                        return obj;
                    });
                    console.dir("[Locations] migrating " + result2.length + " events");
                    i = 0;
                    _d.label = 10;
                case 10:
                    if (!(i < Math.ceil(result2.length / 30000))) return [3 /*break*/, 13];
                    return [4 /*yield*/, app_1.Database.use("sensor_event").bulk({ docs: result2.slice(i * 30000, (i + 1) * 30000) })];
                case 11:
                    out = _d.sent();
                    console.dir(out.filter(function (x) { return !!x.error; }));
                    _d.label = 12;
                case 12:
                    i++;
                    return [3 /*break*/, 10];
                case 13: 
                // UserDevices: [DeviceType, DeviceID<decrypt>, DeviceToken, LastLoginOn, OSVersion, DeviceModel]
                /*
                ALTER TRIGGER TR_UserDevicesLogin
                    ON LAMP.dbo.UserDevices
                    AFTER INSERT, UPDATE
                AS
                BEGIN
                    SET NOCOUNT ON;
                    INSERT INTO LAMP_Aux.dbo.CustomSensorEvent
                    SELECT
                      UserID AS UserID,
                      DATEDIFF_BIG(MS, '1970-01-01', LastLoginOn) AS timestamp,
                      'lamp.analytics' AS sensor_name,
                      (SELECT
                        CASE DeviceType
                          WHEN 1 THEN 'iOS'
                          WHEN 2 THEN 'Android'
                        END AS device_type
                      FOR JSON PATH, WITHOUT_ARRAY_WRAPPER) AS data
                    FROM INSERTED;
                END;
                */
                return [4 /*yield*/, MigratorLink.insert(change_tracking)];
                case 14:
                    // UserDevices: [DeviceType, DeviceID<decrypt>, DeviceToken, LastLoginOn, OSVersion, DeviceModel]
                    /*
                    ALTER TRIGGER TR_UserDevicesLogin
                        ON LAMP.dbo.UserDevices
                        AFTER INSERT, UPDATE
                    AS
                    BEGIN
                        SET NOCOUNT ON;
                        INSERT INTO LAMP_Aux.dbo.CustomSensorEvent
                        SELECT
                          UserID AS UserID,
                          DATEDIFF_BIG(MS, '1970-01-01', LastLoginOn) AS timestamp,
                          'lamp.analytics' AS sensor_name,
                          (SELECT
                            CASE DeviceType
                              WHEN 1 THEN 'iOS'
                              WHEN 2 THEN 'Android'
                            END AS device_type
                          FOR JSON PATH, WITHOUT_ARRAY_WRAPPER) AS data
                        FROM INSERTED;
                    END;
                    */
                    _d.sent();
                    console.dir("sensor_event migration completed");
                    return [3 /*break*/, 17];
                case 15:
                    e_1 = _d.sent();
                    console.error(e_1);
                    return [3 /*break*/, 17];
                case 16:
                    _migrator_active = false;
                    return [7 /*endfinally*/];
                case 17: return [2 /*return*/];
            }
        });
    });
}
exports._migrate_sensor_event = _migrate_sensor_event;
function _migrate_activity_event() {
    var _a;
    return __awaiter(this, void 0, void 0, function () {
        var MigratorLink_1, change_tracking, change_version_1, next_version, _b, _c, _lookup_table_1, _lookup_migrator_id_1, result, all_res, _d, _e, _f, i, out, e_2;
        var _g;
        var _this = this;
        return __generator(this, function (_h) {
            switch (_h.label) {
                case 0:
                    if (_migrator_active) {
                        return [2 /*return*/, console.info("activity_event migration aborted due to existing migrator activity")];
                    }
                    _migrator_active = true;
                    _h.label = 1;
                case 1:
                    _h.trys.push([1, 11, 12, 13]);
                    MigratorLink_1 = app_1.Database.use("root");
                    return [4 /*yield*/, MigratorLink_1.get("#change_tracking")];
                case 2:
                    change_tracking = (_h.sent());
                    change_version_1 = change_tracking.db.activity_event;
                    if (change_version_1 === undefined) {
                        return [2 /*return*/, console.info("activity_event migration aborted due to missing change_version token")];
                    }
                    _c = (_b = Number).parse;
                    return [4 /*yield*/, app_1.SQL.query(templateObject_2 || (templateObject_2 = __makeTemplateObject(["SELECT CHANGE_TRACKING_CURRENT_VERSION()"], ["SELECT CHANGE_TRACKING_CURRENT_VERSION()"])))];
                case 3:
                    next_version = (_a = _c.apply(_b, [(_h.sent()).recordset[0][""]]), (_a !== null && _a !== void 0 ? _a : 0));
                    change_tracking.db.activity_event = next_version;
                    if (next_version - change_version_1 === 0) {
                        return [2 /*return*/, console.info("activity_event migration aborted due to no tracked changes")];
                    }
                    console.dir("activity_event migration from " + (change_version_1 !== null && change_version_1 !== void 0 ? change_version_1 : 0) + " to " + (next_version !== null && next_version !== void 0 ? next_version : 0));
                    return [4 /*yield*/, exports._migrator_lookup_table()];
                case 4:
                    _lookup_table_1 = _h.sent();
                    _lookup_migrator_id_1 = function (legacyID) {
                        var match = _lookup_table_1[legacyID];
                        if (match === undefined) {
                            match = uuid(); // 20-char id for non-Participant objects
                            _lookup_table_1[legacyID] = match;
                            console.log("inserting migrator link: " + legacyID + " => " + match);
                            MigratorLink_1.insert({ _id: "_local/" + legacyID, value: match });
                        }
                        return match;
                    };
                    result = exports.ActivityIndex.map(function (entry) { return __awaiter(_this, void 0, void 0, function () {
                        var events, res;
                        return __generator(this, function (_a) {
                            switch (_a.label) {
                                case 0: return [4 /*yield*/, app_1.SQL.request().query("\n\t\t\t\tSELECT\n          Users.StudyId AS uid,\n          [" + entry.IndexColumnName + "] AS id,\n          DATEDIFF_BIG(MS, '1970-01-01', [" + entry.StartTimeColumnName + "]) AS timestamp,\n          DATEDIFF_BIG(MS, [" + entry.StartTimeColumnName + "], [" + entry.EndTimeColumnName + "]) AS duration,\n          " + (!entry.Slot1Name ? "" : "[" + entry.Slot1ColumnName + "] AS [static_data." + entry.Slot1Name + "],") + "\n          " + (!entry.Slot2Name ? "" : "[" + entry.Slot2ColumnName + "] AS [static_data." + entry.Slot2Name + "],") + "\n          " + (!entry.Slot3Name ? "" : "[" + entry.Slot3ColumnName + "] AS [static_data." + entry.Slot3Name + "],") + "\n          " + (!entry.Slot4Name ? "" : "[" + entry.Slot4ColumnName + "] AS [static_data." + entry.Slot4Name + "],") + "\n          " + (!entry.Slot5Name ? "" : "[" + entry.Slot5ColumnName + "] AS [static_data." + entry.Slot5Name + "],") + "\n          " + (!!entry.TemporalTableName ? "(\n              SELECT\n                  " + (!!entry.Temporal1ColumnName
                                        ? "[" + entry.TemporalTableName + "].[" + entry.Temporal1ColumnName + "]"
                                        : "(NULL)") + " AS item,\n                  " + (!!entry.Temporal2ColumnName
                                        ? "[" + entry.TemporalTableName + "].[" + entry.Temporal2ColumnName + "]"
                                        : "(NULL)") + " AS value,\n                  " + (!!entry.Temporal3ColumnName
                                        ? "[" + entry.TemporalTableName + "].[" + entry.Temporal3ColumnName + "]"
                                        : "(NULL)") + " AS type,\n                  " + (!!entry.Temporal4ColumnName
                                        ? "CAST(CAST([" + entry.TemporalTableName + "].[" + entry.Temporal4ColumnName + "] AS float) * 1000 AS bigint)"
                                        : "(NULL)") + " AS duration,\n                  " + (!!entry.Temporal5ColumnName
                                        ? "[" + entry.TemporalTableName + "].[" + entry.Temporal5ColumnName + "]"
                                        : "(NULL)") + " AS level\n              FROM [" + entry.TemporalTableName + "]\n              WHERE [" + entry.TableName + "].[" + entry.IndexColumnName + "] = [" + entry.TemporalTableName + "].[" + entry.IndexColumnName + "]\n              FOR JSON PATH, INCLUDE_NULL_VALUES\n          ) AS [slices]," : "(NULL) AS [slices],") + "\n          " + (entry.LegacyCTestID !== null ? "(\n            SELECT AdminCTestSettingID \n              FROM Admin_CTestSettings\n              WHERE Admin_CTestSettings.AdminID = Users.AdminID\n                AND Admin_CTestSettings.CTestID = " + entry.LegacyCTestID + "\n            ) AS aid" : "SurveyID AS aid") + "\n        FROM [" + entry.TableName + "]\n        LEFT JOIN Users\n            ON [" + entry.TableName + "].UserID = Users.UserID\n        WHERE [" + entry.IndexColumnName + "] IN (\n          SELECT C.[" + entry.IndexColumnName + "]\n          FROM CHANGETABLE(CHANGES [" + entry.TableName + "], " + (change_version_1 !== null && change_version_1 !== void 0 ? change_version_1 : "NULL") + ") AS C \n          WHERE SYS_CHANGE_CONTEXT IS NULL\n        )\n\t\t\t;")];
                                case 1:
                                    events = (_a.sent()).recordset;
                                    console.dir("[" + entry.TableName + "] migrating " + events.length + " events");
                                    if (events.length === 0)
                                        return [2 /*return*/, []
                                            // Map from SQL DB to the local ActivityEvent type.
                                        ];
                                    res = events.map(function (row) {
                                        var _a, _b, _c;
                                        var activity_event = new ActivityEvent_1.ActivityEvent();
                                        activity_event.timestamp = (_a = Number.parse(row.timestamp), (_a !== null && _a !== void 0 ? _a : 0));
                                        activity_event.duration = (_b = Number.parse(row.duration), (_b !== null && _b !== void 0 ? _b : 0));
                                        activity_event["#parent"] = app_1.Decrypt(row.uid);
                                        // Map internal ID sub-components into the single mangled ID form.
                                        var _activity_original_id = ActivityRepository_1.ActivityRepository._pack_id({
                                            ctest_id: entry.LegacyCTestID !== null ? row.aid : 0,
                                            survey_id: entry.LegacyCTestID === null ? row.aid : 0,
                                            group_id: 0,
                                        });
                                        activity_event.activity = _lookup_migrator_id_1(_activity_original_id);
                                        // Copy static data fields if declared.
                                        activity_event.static_data = {};
                                        if (!!entry.Slot1ColumnName)
                                            activity_event.static_data[entry.Slot1Name] = row["static_data." + entry.Slot1Name];
                                        if (!!entry.Slot2ColumnName)
                                            activity_event.static_data[entry.Slot2Name] = row["static_data." + entry.Slot2Name];
                                        if (!!entry.Slot3ColumnName)
                                            activity_event.static_data[entry.Slot3Name] = row["static_data." + entry.Slot3Name];
                                        if (!!entry.Slot4ColumnName)
                                            activity_event.static_data[entry.Slot4Name] = row["static_data." + entry.Slot4Name];
                                        if (!!entry.Slot5ColumnName)
                                            activity_event.static_data[entry.Slot5Name] = row["static_data." + entry.Slot5Name];
                                        // Decrypt all static data properties if known to be encrypted.
                                        // TODO: Encryption of fields should also be found in the activity index table!
                                        //if (!!activity_event.static_data.survey_name)
                                        //  activity_event.static_data.survey_name = "___DEPRECATED_USE_ACTIVITY_ID_INSTEAD___"
                                        //activity_event.static_data.survey_id = undefined
                                        if (!!activity_event.static_data.drawn_fig_file_name) {
                                            var fname = "file://./_assets/3dfigure/" +
                                                (app_1.Decrypt(activity_event.static_data.drawn_fig_file_name) || activity_event.static_data.drawn_fig_file_name);
                                            activity_event.static_data.drawn_figure = fname; //(await Download(fname)).toString('base64')
                                            activity_event.static_data.drawn_fig_file_name = undefined;
                                        }
                                        if (!!activity_event.static_data.scratch_file_name) {
                                            var fname = "file://./_assets/scratch/" +
                                                (app_1.Decrypt(activity_event.static_data.scratch_file_name) || activity_event.static_data.scratch_file_name);
                                            activity_event.static_data.scratch_figure = fname; //(await Download(fname)).toString('base64')
                                            activity_event.static_data.scratch_file_name = undefined;
                                        }
                                        if (!!activity_event.static_data.game_name)
                                            activity_event.static_data.game_name =
                                                app_1.Decrypt(activity_event.static_data.game_name) || activity_event.static_data.game_name;
                                        if (!!activity_event.static_data.collected_stars)
                                            activity_event.static_data.collected_stars =
                                                app_1.Decrypt(activity_event.static_data.collected_stars) || activity_event.static_data.collected_stars;
                                        if (!!activity_event.static_data.total_jewels_collected)
                                            activity_event.static_data.total_jewels_collected =
                                                app_1.Decrypt(activity_event.static_data.total_jewels_collected) ||
                                                    activity_event.static_data.total_jewels_collected;
                                        if (!!activity_event.static_data.total_bonus_collected)
                                            activity_event.static_data.total_bonus_collected =
                                                app_1.Decrypt(activity_event.static_data.total_bonus_collected) ||
                                                    activity_event.static_data.total_bonus_collected;
                                        if (!!activity_event.static_data.score)
                                            activity_event.static_data.score =
                                                app_1.Decrypt(activity_event.static_data.score) || activity_event.static_data.score;
                                        // Copy all temporal events for this result event by matching parent ID.
                                        activity_event.temporal_slices = JSON.parse((_c = row.slices, (_c !== null && _c !== void 0 ? _c : "[]"))).map(function (slice_row) {
                                            var _a;
                                            var temporal_slice = new ActivityEvent_1.TemporalSlice();
                                            temporal_slice.item = slice_row.item;
                                            temporal_slice.value = slice_row.value;
                                            temporal_slice.type = slice_row.type;
                                            temporal_slice.duration = (_a = Number.parse(slice_row.duration), (_a !== null && _a !== void 0 ? _a : 0));
                                            temporal_slice.level = slice_row.level;
                                            // Special treatment for surveys with encrypted answers.
                                            if (entry.LegacyCTestID === null) {
                                                // survey
                                                temporal_slice.item = app_1.Decrypt(temporal_slice.item) || temporal_slice.item;
                                                temporal_slice.value = app_1.Decrypt(temporal_slice.value) || temporal_slice.value;
                                                temporal_slice.type = !temporal_slice.type ? undefined : temporal_slice.type.toLowerCase();
                                                // Adjust the Likert scaled values to numbers.
                                                if (["Not at all", "12:00AM - 06:00AM", "0-3"].indexOf(temporal_slice.value) >= 0) {
                                                    temporal_slice.value = 0;
                                                }
                                                else if (["Several Times", "06:00AM - 12:00PM", "3-6"].indexOf(temporal_slice.value) >= 0) {
                                                    temporal_slice.value = 1;
                                                }
                                                else if (["More than Half the Time", "12:00PM - 06:00PM", "6-9"].indexOf(temporal_slice.value) >= 0) {
                                                    temporal_slice.value = 2;
                                                }
                                                else if (["Nearly All the Time", "06:00PM - 12:00AM", ">9"].indexOf(temporal_slice.value) >= 0) {
                                                    temporal_slice.value = 3;
                                                }
                                            }
                                            return temporal_slice;
                                        });
                                        // Finally return the newly created event.
                                        return activity_event;
                                    });
                                    return [2 /*return*/, res];
                            }
                        });
                    }); });
                    _e = (_d = (_g = []).concat).apply;
                    _f = [_g];
                    return [4 /*yield*/, Promise.all(result)];
                case 5:
                    all_res = _e.apply(_d, _f.concat([__spread.apply(void 0, [(_h.sent())])]));
                    i = 0;
                    _h.label = 6;
                case 6:
                    if (!(i < Math.ceil(all_res.length / 30000))) return [3 /*break*/, 9];
                    return [4 /*yield*/, app_1.Database.use("activity_event").bulk({ docs: all_res.slice(i * 30000, (i + 1) * 30000) })];
                case 7:
                    out = _h.sent();
                    console.dir(out.filter(function (x) { return !!x.error; }));
                    _h.label = 8;
                case 8:
                    i++;
                    return [3 /*break*/, 6];
                case 9: return [4 /*yield*/, MigratorLink_1.insert(change_tracking)];
                case 10:
                    _h.sent();
                    console.dir("activity_event migration completed");
                    return [3 /*break*/, 13];
                case 11:
                    e_2 = _h.sent();
                    console.error(e_2);
                    return [3 /*break*/, 13];
                case 12:
                    _migrator_active = false;
                    return [7 /*endfinally*/];
                case 13: return [2 /*return*/];
            }
        });
    });
}
exports._migrate_activity_event = _migrate_activity_event;
// old ID (table PK) -> new ID (random UUID)
exports._migrator_lookup_table = function () { return __awaiter(void 0, void 0, void 0, function () {
    return __generator(this, function (_a) {
        switch (_a.label) {
            case 0: return [4 /*yield*/, exports._migrator_dual_table()];
            case 1: return [2 /*return*/, (_a.sent())[0]];
        }
    });
}); };
// new ID (random UUID) -> old ID (table PK)
exports._migrator_export_table = function () { return __awaiter(void 0, void 0, void 0, function () {
    return __generator(this, function (_a) {
        switch (_a.label) {
            case 0: return [4 /*yield*/, exports._migrator_dual_table()];
            case 1: return [2 /*return*/, (_a.sent())[1]];
        }
    });
}); };
// BOTH of the above; more performant as well
exports._migrator_dual_table = function () { return __awaiter(void 0, void 0, void 0, function () {
    var _res, output, _a, _b, x;
    var e_3, _c;
    return __generator(this, function (_d) {
        switch (_d.label) {
            case 0: return [4 /*yield*/, app_1.Database.use("root").baseView("", "", { viewPath: "_local_docs" }, { include_docs: true })];
            case 1:
                _res = _d.sent();
                output = [{}, {}];
                try {
                    for (_a = __values(_res.rows), _b = _a.next(); !_b.done; _b = _a.next()) {
                        x = _b.value;
                        output[0][x.id.replace("_local/", "")] = x.doc.value;
                        output[1][x.doc.value] = x.id.replace("_local/", "");
                    }
                }
                catch (e_3_1) { e_3 = { error: e_3_1 }; }
                finally {
                    try {
                        if (_b && !_b.done && (_c = _a.return)) _c.call(_a);
                    }
                    finally { if (e_3) throw e_3.error; }
                }
                return [2 /*return*/, output];
        }
    });
}); };
// TODO: DELETE
function Identifier_pack(components) {
    if (components.length === 0)
        return "";
    return Buffer.from(components.join(":")).toString("base64").replace(/=/g, "~");
}
// TODO: DELETE
function Activity_pack_id(components) {
    return Identifier_pack(["Activity", components.ctest_id || 0, components.survey_id || 0, components.group_id || 0]);
}
exports.Activity_pack_id = Activity_pack_id;
/**
 *
 */
var toLAMP = function (value) {
    if (!value)
        return [];
    var matches = (app_1.Decrypt(value) || value).toLowerCase().match(/(?:i am )([ \S\/]+)(alone|in [ \S\/]*|with [ \S\/]*)/) || [];
    return [
        {
            home: SensorEvent_1.LocationContext.Home,
            "at home": SensorEvent_1.LocationContext.Home,
            "in school/class": SensorEvent_1.LocationContext.School,
            "at work": SensorEvent_1.LocationContext.Work,
            "in clinic/hospital": SensorEvent_1.LocationContext.Hospital,
            outside: SensorEvent_1.LocationContext.Outside,
            "shopping/dining": SensorEvent_1.LocationContext.Shopping,
            "in bus/train/car": SensorEvent_1.LocationContext.Transit,
        }[(matches[1] || " ").slice(0, -1)],
        {
            alone: SensorEvent_1.SocialContext.Alone,
            "with friends": SensorEvent_1.SocialContext.Friends,
            "with family": SensorEvent_1.SocialContext.Family,
            "with peers": SensorEvent_1.SocialContext.Peers,
            "in crowd": SensorEvent_1.SocialContext.Crowd,
        }[matches[2] || ""],
    ];
};
/**
 *
 */
var fromLAMP = function (value) {
    if (!value[0] && !value[1])
        return undefined;
    return app_1.Encrypt("i am" +
        {
            home: " at home",
            school: " in school/class",
            work: " at work",
            hospital: " in clinic/hospital",
            outside: " outside",
            shopping: " shopping/dining",
            transit: " in bus/train/car",
        }[value[0] || ""] +
        {
            alone: "alone",
            friends: "with friends",
            family: "with family",
            peers: "with peers",
            crowd: "in crowd",
        }[value[1] || ""]);
};
var _decrypt = function (str) {
    var v = app_1.Decrypt(str);
    return !v || v === "" || v === "NA" ? null : v.toLowerCase();
};
var _convert = function (x, strip_suffix, convert_number) {
    if (strip_suffix === void 0) { strip_suffix = ""; }
    if (convert_number === void 0) { convert_number = false; }
    return !x ? null : convert_number ? parseFloat(x.replace(strip_suffix, "")) : x.replace(strip_suffix, "");
};
var _clean = function (x) {
    return x === 0 ? null : x;
};
/**
 *
 */
var HK_to_LAMP = {
    "lamp.height": function (raw) { return ({ value: _convert(_decrypt(raw), " cm", true), units: "cm" }); },
    "lamp.weight": function (raw) { return ({ value: _convert(_decrypt(raw), " kg", true), units: "kg" }); },
    "lamp.heart_rate": function (raw) { return ({ value: _convert(_decrypt(raw), " bpm", true), units: "bpm" }); },
    "lamp.blood_pressure": function (raw) { return ({ value: _convert(_decrypt(raw), " mmhg", false), units: "mmHg" }); },
    "lamp.respiratory_rate": function (raw) { return ({
        value: _convert(_decrypt(raw), " breaths/min", true),
        units: "bpm",
    }); },
    "lamp.sleep": function (raw) { return ({ value: _decrypt(raw), units: "" }); },
    "lamp.steps": function (raw) { return ({ value: _clean(_convert(_decrypt(raw), " steps", true)), units: "steps" }); },
    "lamp.flights": function (raw) { return ({ value: _clean(_convert(_decrypt(raw), " steps", true)), units: "flights" }); },
    "lamp.segment": function (raw) { return ({ value: _convert(_decrypt(raw), "", true), units: "" }); },
    "lamp.distance": function (raw) { return ({ value: _convert(_decrypt(raw), " meters", true), units: "meters" }); },
};
/**
 *
 */
var LAMP_to_HK = {
    // TODO: Consider 0/NA values
    "lamp.height": function (obj) { return app_1.Encrypt(obj.value) + " cm"; },
    "lamp.weight": function (obj) { return app_1.Encrypt(obj.value) + " kg"; },
    "lamp.heart_rate": function (obj) { return app_1.Encrypt(obj.value) + " bpm"; },
    "lamp.blood_pressure": function (obj) { return app_1.Encrypt(obj.value) + " mmhg"; },
    "lamp.respiratory_rate": function (obj) { return app_1.Encrypt(obj.value) + " breaths/min"; },
    "lamp.sleep": function (obj) { return "" + app_1.Encrypt(obj.value); },
    "lamp.steps": function (obj) { return app_1.Encrypt(obj.value) + " steps"; },
    "lamp.flights": function (obj) { return app_1.Encrypt(obj.value) + " steps"; },
    "lamp.segment": function (obj) { return "" + app_1.Encrypt(obj.value); },
    "lamp.distance": function (obj) { return app_1.Encrypt(obj.value) + " meters"; },
};
/**
 *
 */
var HK_LAMP_map = {
    "lamp.height": "Height",
    "lamp.weight": "Weight",
    "lamp.heart_rate": "HeartRate",
    "lamp.blood_pressure": "BloodPressure",
    "lamp.respiratory_rate": "RespiratoryRate",
    "lamp.sleep": "Sleep",
    "lamp.steps": "Steps",
    "lamp.flights": "FlightClimbed",
    "lamp.segment": "Segment",
    "lamp.distance": "Distance",
    Height: "lamp.height",
    Weight: "lamp.weight",
    HeartRate: "lamp.heart_rate",
    BloodPressure: "lamp.blood_pressure",
    RespiratoryRate: "lamp.respiratory_rate",
    Sleep: "lamp.sleep",
    Steps: "lamp.steps",
    FlightClimbed: "lamp.flights",
    Segment: "lamp.segment",
    Distance: "lamp.distance",
};
var _escapeMSSQL = function (val) {
    return val.replace(/[\0\n\r\b\t\\'"\x1a]/g, function (s) {
        switch (s) {
            case "\0":
                return "\\0";
            case "\n":
                return "\\n";
            case "\r":
                return "\\r";
            case "\b":
                return "\\b";
            case "\t":
                return "\\t";
            case "\x1a":
                return "\\Z";
            case "'":
                return "''";
            case '"':
                return '""';
            default:
                return "\\" + s;
        }
    });
};
exports.ActivityIndex = [
    {
        ActivityIndexID: "1",
        Name: "lamp.survey",
        TableName: "SurveyResult",
        IndexColumnName: "SurveyResultID",
        StartTimeColumnName: "StartTime",
        EndTimeColumnName: "EndTime",
        Slot1Name: null,
        Slot1ColumnName: null,
        Slot2Name: null,
        Slot2ColumnName: null,
        Slot3Name: null,
        Slot3ColumnName: null,
        Slot4Name: null,
        Slot4ColumnName: null,
        Slot5Name: null,
        Slot5ColumnName: null,
        TemporalTableName: "SurveyResultDtl",
        TemporalIndexColumnName: "SurveyResultDtlID",
        Temporal1ColumnName: "Question",
        Temporal2ColumnName: "CorrectAnswer",
        Temporal3ColumnName: "ClickRange",
        Temporal4ColumnName: "TimeTaken",
        Temporal5ColumnName: null,
        SettingsSlots: "",
        SettingsDefaults: null,
        LegacyCTestID: null,
    },
    {
        ActivityIndexID: "2",
        Name: "lamp.nback",
        TableName: "CTest_NBackResult",
        IndexColumnName: "NBackResultID",
        StartTimeColumnName: "StartTime",
        EndTimeColumnName: "EndTime",
        Slot1Name: "score",
        Slot1ColumnName: "Score",
        Slot2Name: "correct_answers",
        Slot2ColumnName: "CorrectAnswers",
        Slot3Name: "wrong_answers",
        Slot3ColumnName: "WrongAnswers",
        Slot4Name: "total_questions",
        Slot4ColumnName: "TotalQuestions",
        Slot5Name: "version",
        Slot5ColumnName: "Version",
        TemporalTableName: null,
        TemporalIndexColumnName: null,
        Temporal1ColumnName: null,
        Temporal2ColumnName: null,
        Temporal3ColumnName: null,
        Temporal4ColumnName: null,
        Temporal5ColumnName: null,
        SettingsSlots: "",
        SettingsDefaults: null,
        LegacyCTestID: 1,
    },
    {
        ActivityIndexID: "3",
        Name: "lamp.trails_b",
        TableName: "CTest_TrailsBResult",
        IndexColumnName: "TrailsBResultID",
        StartTimeColumnName: "StartTime",
        EndTimeColumnName: "EndTime",
        Slot1Name: "point",
        Slot1ColumnName: "Point",
        Slot2Name: "rating",
        Slot2ColumnName: "Rating",
        Slot3Name: "score",
        Slot3ColumnName: "Score",
        Slot4Name: "total_attempts",
        Slot4ColumnName: "TotalAttempts",
        Slot5Name: null,
        Slot5ColumnName: null,
        TemporalTableName: "CTest_TrailsBResultDtl",
        TemporalIndexColumnName: "TrailsBResultDtlID",
        Temporal1ColumnName: "Alphabet",
        Temporal2ColumnName: null,
        Temporal3ColumnName: "Status",
        Temporal4ColumnName: "TimeTaken",
        Temporal5ColumnName: "Sequence",
        SettingsSlots: "",
        SettingsDefaults: null,
        LegacyCTestID: 2,
    },
    {
        ActivityIndexID: "4",
        Name: "lamp.spatial_span",
        TableName: "CTest_SpatialResult",
        IndexColumnName: "SpatialResultID",
        StartTimeColumnName: "StartTime",
        EndTimeColumnName: "EndTime",
        Slot1Name: "point",
        Slot1ColumnName: "Point",
        Slot2Name: "score",
        Slot2ColumnName: "Score",
        Slot3Name: "correct_answers",
        Slot3ColumnName: "CorrectAnswers",
        Slot4Name: "wrong_answers",
        Slot4ColumnName: "WrongAnswers",
        Slot5Name: "type",
        Slot5ColumnName: "Type",
        TemporalTableName: "CTest_SpatialResultDtl",
        TemporalIndexColumnName: "SpatialResultDtlID",
        Temporal1ColumnName: "GameIndex",
        Temporal2ColumnName: "Sequence",
        Temporal3ColumnName: "Status",
        Temporal4ColumnName: "TimeTaken",
        Temporal5ColumnName: "Level",
        SettingsSlots: "",
        SettingsDefaults: null,
        LegacyCTestID: 3,
    },
    {
        ActivityIndexID: "5",
        Name: "lamp.simple_memory",
        TableName: "CTest_SimpleMemoryResult",
        IndexColumnName: "SimpleMemoryResultID",
        StartTimeColumnName: "StartTime",
        EndTimeColumnName: "EndTime",
        Slot1Name: "score",
        Slot1ColumnName: "Score",
        Slot2Name: "correct_answers",
        Slot2ColumnName: "CorrectAnswers",
        Slot3Name: "wrong_answers",
        Slot3ColumnName: "WrongAnswers",
        Slot4Name: "total_questions",
        Slot4ColumnName: "TotalQuestions",
        Slot5Name: "version",
        Slot5ColumnName: "Version",
        TemporalTableName: null,
        TemporalIndexColumnName: null,
        Temporal1ColumnName: null,
        Temporal2ColumnName: null,
        Temporal3ColumnName: null,
        Temporal4ColumnName: null,
        Temporal5ColumnName: null,
        SettingsSlots: "",
        SettingsDefaults: null,
        LegacyCTestID: 5,
    },
    {
        ActivityIndexID: "6",
        Name: "lamp.serial7s",
        TableName: "CTest_Serial7Result",
        IndexColumnName: "Serial7ResultID",
        StartTimeColumnName: "StartTime",
        EndTimeColumnName: "EndTime",
        Slot1Name: "point",
        Slot1ColumnName: "Point",
        Slot2Name: "score",
        Slot2ColumnName: "Score",
        Slot3Name: "total_attempts",
        Slot3ColumnName: "TotalAttempts",
        Slot4Name: "total_questions",
        Slot4ColumnName: "TotalQuestions",
        Slot5Name: "version",
        Slot5ColumnName: "Version",
        TemporalTableName: null,
        TemporalIndexColumnName: null,
        Temporal1ColumnName: null,
        Temporal2ColumnName: null,
        Temporal3ColumnName: null,
        Temporal4ColumnName: null,
        Temporal5ColumnName: null,
        SettingsSlots: "",
        SettingsDefaults: null,
        LegacyCTestID: 6,
    },
    {
        ActivityIndexID: "7",
        Name: "lamp.cats_and_dogs",
        TableName: "CTest_CatAndDogResult",
        IndexColumnName: "CatAndDogResultID",
        StartTimeColumnName: "StartTime",
        EndTimeColumnName: "EndTime",
        Slot1Name: "point",
        Slot1ColumnName: "Point",
        Slot2Name: "rating",
        Slot2ColumnName: "Rating",
        Slot3Name: "correct_answers",
        Slot3ColumnName: "CorrectAnswers",
        Slot4Name: "wrong_answers",
        Slot4ColumnName: "WrongAnswers",
        Slot5Name: "total_questions",
        Slot5ColumnName: "TotalQuestions",
        TemporalTableName: null,
        TemporalIndexColumnName: null,
        Temporal1ColumnName: null,
        Temporal2ColumnName: null,
        Temporal3ColumnName: null,
        Temporal4ColumnName: null,
        Temporal5ColumnName: null,
        SettingsSlots: "",
        SettingsDefaults: null,
        LegacyCTestID: 7,
    },
    {
        ActivityIndexID: "8",
        Name: "lamp.3d_figure_copy",
        TableName: "CTest_3DFigureResult",
        IndexColumnName: "3DFigureResultID",
        StartTimeColumnName: "StartTime",
        EndTimeColumnName: "EndTime",
        Slot1Name: "point",
        Slot1ColumnName: "Point",
        Slot2Name: "drawn_fig_file_name",
        Slot2ColumnName: "DrawnFigFileName",
        Slot3Name: "game_name",
        Slot3ColumnName: "GameName",
        Slot4Name: null,
        Slot4ColumnName: null,
        Slot5Name: null,
        Slot5ColumnName: null,
        TemporalTableName: null,
        TemporalIndexColumnName: null,
        Temporal1ColumnName: null,
        Temporal2ColumnName: null,
        Temporal3ColumnName: null,
        Temporal4ColumnName: null,
        Temporal5ColumnName: null,
        SettingsSlots: "",
        SettingsDefaults: null,
        LegacyCTestID: 8,
    },
    {
        ActivityIndexID: "9",
        Name: "lamp.visual_association",
        TableName: "CTest_VisualAssociationResult",
        IndexColumnName: "VisualAssocResultID",
        StartTimeColumnName: "StartTime",
        EndTimeColumnName: "EndTime",
        Slot1Name: "point",
        Slot1ColumnName: "Point",
        Slot2Name: "score",
        Slot2ColumnName: "Score",
        Slot3Name: "total_attempts",
        Slot3ColumnName: "TotalAttempts",
        Slot4Name: "total_questions",
        Slot4ColumnName: "TotalQuestions",
        Slot5Name: "version",
        Slot5ColumnName: "Version",
        TemporalTableName: null,
        TemporalIndexColumnName: null,
        Temporal1ColumnName: null,
        Temporal2ColumnName: null,
        Temporal3ColumnName: null,
        Temporal4ColumnName: null,
        Temporal5ColumnName: null,
        SettingsSlots: "",
        SettingsDefaults: null,
        LegacyCTestID: 9,
    },
    {
        ActivityIndexID: "10",
        Name: "lamp.digit_span",
        TableName: "CTest_DigitSpanResult",
        IndexColumnName: "DigitSpanResultID",
        StartTimeColumnName: "StartTime",
        EndTimeColumnName: "EndTime",
        Slot1Name: "point",
        Slot1ColumnName: "Point",
        Slot2Name: "score",
        Slot2ColumnName: "Score",
        Slot3Name: "correct_answers",
        Slot3ColumnName: "CorrectAnswers",
        Slot4Name: "wrong_answers",
        Slot4ColumnName: "WrongAnswers",
        Slot5Name: "type",
        Slot5ColumnName: "Type",
        TemporalTableName: null,
        TemporalIndexColumnName: null,
        Temporal1ColumnName: null,
        Temporal2ColumnName: null,
        Temporal3ColumnName: null,
        Temporal4ColumnName: null,
        Temporal5ColumnName: null,
        SettingsSlots: "",
        SettingsDefaults: null,
        LegacyCTestID: 10,
    },
    {
        ActivityIndexID: "11",
        Name: "lamp.cats_and_dogs_new",
        TableName: "CTest_CatAndDogNewResult",
        IndexColumnName: "CatAndDogNewResultID",
        StartTimeColumnName: "StartTime",
        EndTimeColumnName: "EndTime",
        Slot1Name: "point",
        Slot1ColumnName: "Point",
        Slot2Name: "score",
        Slot2ColumnName: "Score",
        Slot3Name: "correct_answers",
        Slot3ColumnName: "CorrectAnswers",
        Slot4Name: "wrong_answers",
        Slot4ColumnName: "WrongAnswers",
        Slot5Name: null,
        Slot5ColumnName: null,
        TemporalTableName: null,
        TemporalIndexColumnName: null,
        Temporal1ColumnName: null,
        Temporal2ColumnName: null,
        Temporal3ColumnName: null,
        Temporal4ColumnName: null,
        Temporal5ColumnName: null,
        SettingsSlots: "",
        SettingsDefaults: null,
        LegacyCTestID: 11,
    },
    {
        ActivityIndexID: "12",
        Name: "lamp.temporal_order",
        TableName: "CTest_TemporalOrderResult",
        IndexColumnName: "TemporalOrderResultID",
        StartTimeColumnName: "StartTime",
        EndTimeColumnName: "EndTime",
        Slot1Name: "point",
        Slot1ColumnName: "Point",
        Slot2Name: "score",
        Slot2ColumnName: "Score",
        Slot3Name: "correct_answers",
        Slot3ColumnName: "CorrectAnswers",
        Slot4Name: "wrong_answers",
        Slot4ColumnName: "WrongAnswers",
        Slot5Name: "version",
        Slot5ColumnName: "Version",
        TemporalTableName: null,
        TemporalIndexColumnName: null,
        Temporal1ColumnName: null,
        Temporal2ColumnName: null,
        Temporal3ColumnName: null,
        Temporal4ColumnName: null,
        Temporal5ColumnName: null,
        SettingsSlots: "",
        SettingsDefaults: null,
        LegacyCTestID: 12,
    },
    {
        ActivityIndexID: "13",
        Name: "lamp.nback_new",
        TableName: "CTest_NBackNewResult",
        IndexColumnName: "NBackNewResultID",
        StartTimeColumnName: "StartTime",
        EndTimeColumnName: "EndTime",
        Slot1Name: "point",
        Slot1ColumnName: "Point",
        Slot2Name: "score",
        Slot2ColumnName: "Score",
        Slot3Name: "correct_answers",
        Slot3ColumnName: "CorrectAnswers",
        Slot4Name: "wrong_answers",
        Slot4ColumnName: "WrongAnswers",
        Slot5Name: "total_questions",
        Slot5ColumnName: "TotalQuestions",
        TemporalTableName: null,
        TemporalIndexColumnName: null,
        Temporal1ColumnName: null,
        Temporal2ColumnName: null,
        Temporal3ColumnName: null,
        Temporal4ColumnName: null,
        Temporal5ColumnName: null,
        SettingsSlots: "",
        SettingsDefaults: null,
        LegacyCTestID: 14,
    },
    {
        ActivityIndexID: "14",
        Name: "lamp.trails_b_new",
        TableName: "CTest_TrailsBNewResult",
        IndexColumnName: "TrailsBNewResultID",
        StartTimeColumnName: "StartTime",
        EndTimeColumnName: "EndTime",
        Slot1Name: "point",
        Slot1ColumnName: "Point",
        Slot2Name: "rating",
        Slot2ColumnName: "Rating",
        Slot3Name: "score",
        Slot3ColumnName: "Score",
        Slot4Name: "total_attempts",
        Slot4ColumnName: "TotalAttempts",
        Slot5Name: "version",
        Slot5ColumnName: "Version",
        TemporalTableName: "CTest_TrailsBNewResultDtl",
        TemporalIndexColumnName: "TrailsBNewResultDtlID",
        Temporal1ColumnName: "Alphabet",
        Temporal2ColumnName: null,
        Temporal3ColumnName: "Status",
        Temporal4ColumnName: "TimeTaken",
        Temporal5ColumnName: "Sequence",
        SettingsSlots: "",
        SettingsDefaults: null,
        LegacyCTestID: 15,
    },
    {
        ActivityIndexID: "15",
        Name: "lamp.trails_b_dot_touch",
        TableName: "CTest_TrailsBDotTouchResult",
        IndexColumnName: "TrailsBDotTouchResultID",
        StartTimeColumnName: "StartTime",
        EndTimeColumnName: "EndTime",
        Slot1Name: "point",
        Slot1ColumnName: "Point",
        Slot2Name: "rating",
        Slot2ColumnName: "Rating",
        Slot3Name: "score",
        Slot3ColumnName: "Score",
        Slot4Name: "total_attempts",
        Slot4ColumnName: "TotalAttempts",
        Slot5Name: null,
        Slot5ColumnName: null,
        TemporalTableName: "CTest_TrailsBDotTouchResultDtl",
        TemporalIndexColumnName: "TrailsBDotTouchResultDtlID",
        Temporal1ColumnName: "Alphabet",
        Temporal2ColumnName: null,
        Temporal3ColumnName: "Status",
        Temporal4ColumnName: "TimeTaken",
        Temporal5ColumnName: "Sequence",
        SettingsSlots: "",
        SettingsDefaults: null,
        LegacyCTestID: 16,
    },
    {
        ActivityIndexID: "16",
        Name: "lamp.jewels_a",
        TableName: "CTest_JewelsTrailsAResult",
        IndexColumnName: "JewelsTrailsAResultID",
        StartTimeColumnName: "StartTime",
        EndTimeColumnName: "EndTime",
        Slot1Name: "point",
        Slot1ColumnName: "Point",
        Slot2Name: "score",
        Slot2ColumnName: "Score",
        Slot3Name: "total_attempts",
        Slot3ColumnName: "TotalAttempts",
        Slot4Name: "total_bonus_collected",
        Slot4ColumnName: "TotalBonusCollected",
        Slot5Name: "total_jewels_collected",
        Slot5ColumnName: "TotalJewelsCollected",
        TemporalTableName: "CTest_JewelsTrailsAResultDtl",
        TemporalIndexColumnName: "JewelsTrailsAResultDtlID",
        Temporal1ColumnName: "Alphabet",
        Temporal2ColumnName: null,
        Temporal3ColumnName: "Status",
        Temporal4ColumnName: "TimeTaken",
        Temporal5ColumnName: "Sequence",
        SettingsSlots: "",
        SettingsDefaults: null,
        LegacyCTestID: 17,
    },
    {
        ActivityIndexID: "17",
        Name: "lamp.jewels_b",
        TableName: "CTest_JewelsTrailsBResult",
        IndexColumnName: "JewelsTrailsBResultID",
        StartTimeColumnName: "StartTime",
        EndTimeColumnName: "EndTime",
        Slot1Name: "point",
        Slot1ColumnName: "Point",
        Slot2Name: "score",
        Slot2ColumnName: "Score",
        Slot3Name: "total_attempts",
        Slot3ColumnName: "TotalAttempts",
        Slot4Name: "total_bonus_collected",
        Slot4ColumnName: "TotalBonusCollected",
        Slot5Name: "total_jewels_collected",
        Slot5ColumnName: "TotalJewelsCollected",
        TemporalTableName: "CTest_JewelsTrailsBResultDtl",
        TemporalIndexColumnName: "JewelsTrailsBResultDtlID",
        Temporal1ColumnName: "Alphabet",
        Temporal2ColumnName: null,
        Temporal3ColumnName: "Status",
        Temporal4ColumnName: "TimeTaken",
        Temporal5ColumnName: "Sequence",
        SettingsSlots: "",
        SettingsDefaults: null,
        LegacyCTestID: 18,
    },
    {
        ActivityIndexID: "18",
        Name: "lamp.scratch_image",
        TableName: "CTest_ScratchImageResult",
        IndexColumnName: "ScratchImageResultID",
        StartTimeColumnName: "StartTime",
        EndTimeColumnName: "EndTime",
        Slot1Name: "point",
        Slot1ColumnName: "Point",
        Slot2Name: "scratch_file_name",
        Slot2ColumnName: "DrawnFigFileName",
        Slot3Name: "game_name",
        Slot3ColumnName: "GameName",
        Slot4Name: null,
        Slot4ColumnName: null,
        Slot5Name: null,
        Slot5ColumnName: null,
        TemporalTableName: null,
        TemporalIndexColumnName: null,
        Temporal1ColumnName: null,
        Temporal2ColumnName: null,
        Temporal3ColumnName: null,
        Temporal4ColumnName: null,
        Temporal5ColumnName: null,
        SettingsSlots: null,
        SettingsDefaults: null,
        LegacyCTestID: 19,
    },
    {
        ActivityIndexID: "19",
        Name: "lamp.spin_wheel",
        TableName: "CTest_SpinWheelResult",
        IndexColumnName: "SpinWheelResultID",
        StartTimeColumnName: "StartTime",
        EndTimeColumnName: "GameDate",
        Slot1Name: "collected_stars",
        Slot1ColumnName: "CollectedStars",
        Slot2Name: "day_streak",
        Slot2ColumnName: "DayStreak",
        Slot3Name: "streak_spin",
        Slot3ColumnName: "StrakSpin",
        Slot4Name: null,
        Slot4ColumnName: null,
        Slot5Name: null,
        Slot5ColumnName: null,
        TemporalTableName: null,
        TemporalIndexColumnName: null,
        Temporal1ColumnName: null,
        Temporal2ColumnName: null,
        Temporal3ColumnName: null,
        Temporal4ColumnName: null,
        Temporal5ColumnName: null,
        SettingsSlots: null,
        SettingsDefaults: null,
        LegacyCTestID: 20,
    },
];
var templateObject_1, templateObject_2;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoibWlncmF0ZS5qcyIsInNvdXJjZVJvb3QiOiIiLCJzb3VyY2VzIjpbIi4uLy4uL3NyYy9yZXBvc2l0b3J5L21pZ3JhdGUudHMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6Ijs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7OztBQUFBLDhCQUF3RDtBQUN4RCxvREFBOEY7QUFDOUYsd0RBQXFFO0FBQ3JFLHVFQUFxRTtBQUNyRSxpQ0FBdUM7QUFFdkMsSUFBTSxJQUFJLEdBQUcsdUJBQWMsQ0FBQyxrQ0FBa0MsRUFBRSxFQUFFLENBQUMsQ0FBQSxDQUFDLGVBQWU7QUFFbkYscUNBQXFDO0FBRXJDLGFBQWE7QUFDYixzQ0FBc0M7QUFDdEMsOENBQThDO0FBQzlDLElBQUksZ0JBQWdCLEdBQUcsS0FBSyxDQUFBO0FBWTVCLE1BQU0sQ0FBQyxjQUFjLENBQUMsTUFBTSxFQUFFLE9BQU8sRUFBRTtJQUNyQyxLQUFLLEVBQUUsVUFBVSxLQUF5QztRQUN4RCxJQUFJLEtBQUssS0FBSyxJQUFJLElBQUksS0FBSyxLQUFLLFNBQVM7WUFBRSxPQUFPLFNBQVMsQ0FBQTtRQUMzRCxJQUFJLE9BQU8sS0FBSyxLQUFLLFFBQVE7WUFBRSxPQUFPLEtBQUssQ0FBQTtRQUMzQyxPQUFPLEtBQUssQ0FBQyxNQUFNLENBQUMsS0FBSyxDQUFDLENBQUMsQ0FBQyxDQUFDLENBQUMsU0FBUyxDQUFDLENBQUMsQ0FBQyxNQUFNLENBQUMsS0FBSyxDQUFDLENBQUE7SUFDekQsQ0FBQztDQUNGLENBQUMsQ0FBQTtBQUVGLEdBQUc7QUFDSCxHQUFHO0FBQ0gsR0FBRztBQUVILFNBQXNCLHFCQUFxQjs7Ozs7OztvQkFDekMsSUFBSSxnQkFBZ0IsRUFBRTt3QkFDcEIsc0JBQU8sT0FBTyxDQUFDLElBQUksQ0FBQyxrRUFBa0UsQ0FBQyxFQUFBO3FCQUN4RjtvQkFDRCxnQkFBZ0IsR0FBRyxJQUFJLENBQUE7Ozs7b0JBRWYs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