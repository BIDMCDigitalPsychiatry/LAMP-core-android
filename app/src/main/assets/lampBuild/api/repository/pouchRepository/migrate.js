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
var app_1 = require("../../app");
var SensorEvent_1 = require("../../model/SensorEvent");
var ActivityEvent_1 = require("../../model/ActivityEvent");
var ActivityRepository_1 = require("../../repository/ActivityRepository");
var nanoid_1 = require("nanoid");
var uuid = nanoid_1.customAlphabet("1234567890abcdefghjkmnpqrstvwxyz", 20); // crockford-32
// TODO: Switch to _local/ documents!
// IMPORTANT!
// This isn't synchronized/atomic yet.
// Maybe this needs to be a job queue instead.
var _migrator_active = false;
// Object.defineProperty(Number, "parse", {
//   value: function (input: string | number | undefined | null): number | undefined {
//     if (input === null || input === undefined) return undefined
//     if (typeof input === "number") return input
//     return isNaN(Number(input)) ? undefined : Number(input)
//   },
// })
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
                console.log(_res.rows);
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
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoibWlncmF0ZS5qcyIsInNvdXJjZVJvb3QiOiIiLCJzb3VyY2VzIjpbIi4uLy4uLy4uL3NyYy9yZXBvc2l0b3J5L3BvdWNoUmVwb3NpdG9yeS9taWdyYXRlLnRzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiI7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7QUFBQSxpQ0FBMkQ7QUFDM0QsdURBQWlHO0FBQ2pHLDJEQUF3RTtBQUN4RSwwRUFBd0U7QUFDeEUsaUNBQXVDO0FBRXZDLElBQU0sSUFBSSxHQUFHLHVCQUFjLENBQUMsa0NBQWtDLEVBQUUsRUFBRSxDQUFDLENBQUEsQ0FBQyxlQUFlO0FBRW5GLHFDQUFxQztBQUVyQyxhQUFhO0FBQ2Isc0NBQXNDO0FBQ3RDLDhDQUE4QztBQUM5QyxJQUFJLGdCQUFnQixHQUFHLEtBQUssQ0FBQTtBQVk1QiwyQ0FBMkM7QUFDM0Msc0ZBQXNGO0FBQ3RGLGtFQUFrRTtBQUNsRSxrREFBa0Q7QUFDbEQsOERBQThEO0FBQzlELE9BQU87QUFDUCxLQUFLO0FBRUwsR0FBRztBQUNILEdBQUc7QUFDSCxHQUFHO0FBRUgsU0FBc0IscUJBQXFCOzs7Ozs7O29CQUN6QyxJQUFJLGdCQUFnQixFQUFFO3dCQUNwQixzQkFBTyxPQUFPLENBQUMsSUFBSSxDQUFDLGtFQUFrRSxDQUFDLEVBQUE7cUJBQ3hGO29CQUNELGdCQUFnQixHQUFHLElBQUksQ0FBQTs7OztvQkFFZixZQUFZLEdBQUcsY0FBUSxDQUFDLEdBQUcsQ0FBQyxNQUFNLENBQUMsQ0FBQTtvQkFFaEIscUJBQU0sWUFBWSxDQUFDLEdBQUcsQ0FBQyxrQkFBa0IsQ0FBQyxFQUFBOztvQkFBN0QsZUFBZSxHQUFHLENBQUMsU0FBMEMsQ0FBUTtvQkFDckUsY0FBYyxHQUFXLGVBQWUsQ0FBQyxFQUFFLENBQUMsWUFBWSxDQUFBO29CQUM5RCxJQUFJLGNBQWMsS0FBSyxTQUFTLEVBQUU7d0JBQ2hDLHNC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