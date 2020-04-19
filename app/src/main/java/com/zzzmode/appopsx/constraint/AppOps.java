package com.zzzmode.appopsx.constraint;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Source: https://android.googlesource.com/platform/frameworks/base/+/master/core/java/android/app/AppOpsManager.java
 */
public class AppOps {
    public static final List<Integer> sOpToSwitch;
    public static final List<String> sOpNames;
    public static final List<String> sOpPerms;
    public static final List<Integer> ALWAYS_SHOWN_OP;
    public static final List<String> PERMISSION_GROUP_ORDER;
    public static final Map<Integer, String> OP_CUSTOM_PERMISSION_GROUP_MAP;

    public static final int OP_NONE = -1;
    public static final int OP_COARSE_LOCATION = 0;
    public static final int OP_FINE_LOCATION = 1;
    public static final int OP_GPS = 2;
    public static final int OP_VIBRATE = 3;
    public static final int OP_READ_CONTACTS = 4;
    public static final int OP_WRITE_CONTACTS = 5;
    public static final int OP_READ_CALL_LOG = 6;
    public static final int OP_WRITE_CALL_LOG = 7;
    public static final int OP_READ_CALENDAR = 8;
    public static final int OP_WRITE_CALENDAR = 9;
    public static final int OP_WIFI_SCAN = 10;
    public static final int OP_POST_NOTIFICATION = 11;
    public static final int OP_NEIGHBORING_CELLS = 12;
    public static final int OP_CALL_PHONE = 13;
    public static final int OP_READ_SMS = 14;
    public static final int OP_WRITE_SMS = 15;
    public static final int OP_RECEIVE_SMS = 16;
    public static final int OP_RECEIVE_EMERGECY_SMS = 17;
    public static final int OP_RECEIVE_MMS = 18;
    public static final int OP_RECEIVE_WAP_PUSH = 19;
    public static final int OP_SEND_SMS = 20;
    public static final int OP_READ_ICC_SMS = 21;
    public static final int OP_WRITE_ICC_SMS = 22;
    public static final int OP_WRITE_SETTINGS = 23;
    public static final int OP_SYSTEM_ALERT_WINDOW = 24;
    public static final int OP_ACCESS_NOTIFICATIONS = 25;
    public static final int OP_CAMERA = 26;
    public static final int OP_RECORD_AUDIO = 27;
    public static final int OP_PLAY_AUDIO = 28;
    public static final int OP_READ_CLIPBOARD = 29;
    public static final int OP_WRITE_CLIPBOARD = 30;
    public static final int OP_TAKE_MEDIA_BUTTONS = 31;
    public static final int OP_TAKE_AUDIO_FOCUS = 32;
    public static final int OP_AUDIO_MASTER_VOLUME = 33;
    public static final int OP_AUDIO_VOICE_VOLUME = 34;
    public static final int OP_AUDIO_RING_VOLUME = 35;
    public static final int OP_AUDIO_MEDIA_VOLUME = 36;
    public static final int OP_AUDIO_ALARM_VOLUME = 37;
    public static final int OP_AUDIO_NOTIFICATION_VOLUME = 38;
    public static final int OP_AUDIO_BLUETOOTH_VOLUME = 39;
    public static final int OP_WAKE_LOCK = 40;
    public static final int OP_MONITOR_LOCATION = 41;
    public static final int OP_MONITOR_HIGH_POWER_LOCATION = 42;
    public static final int OP_GET_USAGE_STATS = 43;
    public static final int OP_MUTE_MICROPHONE = 44;
    public static final int OP_TOAST_WINDOW = 45;
    public static final int OP_PROJECT_MEDIA = 46;
    public static final int OP_ACTIVATE_VPN = 47;
    public static final int OP_WRITE_WALLPAPER = 48;
    public static final int OP_ASSIST_STRUCTURE = 49;
    public static final int OP_ASSIST_SCREENSHOT = 50;
    public static final int OP_READ_PHONE_STATE = 51;
    public static final int OP_ADD_VOICEMAIL = 52;
    public static final int OP_USE_SIP = 53;
    public static final int OP_PROCESS_OUTGOING_CALLS = 54;
    public static final int OP_USE_FINGERPRINT = 55;
    public static final int OP_BODY_SENSORS = 56;
    public static final int OP_READ_CELL_BROADCASTS = 57;
    public static final int OP_MOCK_LOCATION = 58;
    public static final int OP_READ_EXTERNAL_STORAGE = 59;
    public static final int OP_WRITE_EXTERNAL_STORAGE = 60;
    public static final int OP_TURN_SCREEN_ON = 61;
    public static final int OP_GET_ACCOUNTS = 62;
    public static final int OP_RUN_IN_BACKGROUND = 63;
    public static final int OP_AUDIO_ACCESSIBILITY_VOLUME = 64;
    public static final int OP_READ_PHONE_NUMBERS = 65;
    public static final int OP_REQUEST_INSTALL_PACKAGES = 66;
    public static final int OP_PICTURE_IN_PICTURE = 67;
    public static final int OP_INSTANT_APP_START_FOREGROUND = 68;
    public static final int OP_ANSWER_PHONE_CALLS = 69;
    public static final int OP_RUN_ANY_IN_BACKGROUND = 70;
    public static final int OP_CHANGE_WIFI_STATE = 71;
    public static final int OP_REQUEST_DELETE_PACKAGES = 72;
    public static final int OP_BIND_ACCESSIBILITY_SERVICE = 73;
    public static final int OP_ACCEPT_HANDOVER = 74;
    public static final int OP_MANAGE_IPSEC_TUNNELS = 75;
    public static final int OP_START_FOREGROUND = 76;
    public static final int OP_BLUETOOTH_SCAN = 77;
    public static final int OP_USE_BIOMETRIC = 78;
    public static final int OP_ACTIVITY_RECOGNITION = 79;
    public static final int OP_SMS_FINANCIAL_TRANSACTIONS = 80;
    public static final int OP_READ_MEDIA_AUDIO = 81;
    public static final int OP_WRITE_MEDIA_AUDIO = 82;
    public static final int OP_READ_MEDIA_VIDEO = 83;
    public static final int OP_WRITE_MEDIA_VIDEO = 84;
    public static final int OP_READ_MEDIA_IMAGES = 85;
    public static final int OP_WRITE_MEDIA_IMAGES = 86;
    public static final int OP_LEGACY_STORAGE = 87;
    public static final int OP_ACCESS_ACCESSIBILITY = 88;
    public static final int OP_READ_DEVICE_IDENTIFIERS = 89;
    public static final int OP_ACCESS_MEDIA_LOCATION = 90;
    public static final int OP_ACTIVATE_PLATFORM_VPN = 91;
    public static final int _NUM_OP = 92;

    /**
     * This maps each operation to the operation that serves as the
     * switch to determine whether it is allowed.  Generally this is
     * a 1:1 mapping, but for some things (like location) that have
     * multiple low-level operations being tracked that should be
     * presented to the user as one switch then this can be used to
     * make them all controlled by the same single operation.
     */
    private static Integer[] _sOpToSwitch = new Integer[] {
            OP_COARSE_LOCATION,                 // COARSE_LOCATION
            OP_COARSE_LOCATION,                 // FINE_LOCATION
            OP_COARSE_LOCATION,                 // GPS
            OP_VIBRATE,                         // VIBRATE
            OP_READ_CONTACTS,                   // READ_CONTACTS
            OP_WRITE_CONTACTS,                  // WRITE_CONTACTS
            OP_READ_CALL_LOG,                   // READ_CALL_LOG
            OP_WRITE_CALL_LOG,                  // WRITE_CALL_LOG
            OP_READ_CALENDAR,                   // READ_CALENDAR
            OP_WRITE_CALENDAR,                  // WRITE_CALENDAR
            OP_COARSE_LOCATION,                 // WIFI_SCAN
            OP_POST_NOTIFICATION,               // POST_NOTIFICATION
            OP_COARSE_LOCATION,                 // NEIGHBORING_CELLS
            OP_CALL_PHONE,                      // CALL_PHONE
            OP_READ_SMS,                        // READ_SMS
            OP_WRITE_SMS,                       // WRITE_SMS
            OP_RECEIVE_SMS,                     // RECEIVE_SMS
            OP_RECEIVE_SMS,                     // RECEIVE_EMERGECY_SMS
            OP_RECEIVE_MMS,                     // RECEIVE_MMS
            OP_RECEIVE_WAP_PUSH,                // RECEIVE_WAP_PUSH
            OP_SEND_SMS,                        // SEND_SMS
            OP_READ_SMS,                        // READ_ICC_SMS
            OP_WRITE_SMS,                       // WRITE_ICC_SMS
            OP_WRITE_SETTINGS,                  // WRITE_SETTINGS
            OP_SYSTEM_ALERT_WINDOW,             // SYSTEM_ALERT_WINDOW
            OP_ACCESS_NOTIFICATIONS,            // ACCESS_NOTIFICATIONS
            OP_CAMERA,                          // CAMERA
            OP_RECORD_AUDIO,                    // RECORD_AUDIO
            OP_PLAY_AUDIO,                      // PLAY_AUDIO
            OP_READ_CLIPBOARD,                  // READ_CLIPBOARD
            OP_WRITE_CLIPBOARD,                 // WRITE_CLIPBOARD
            OP_TAKE_MEDIA_BUTTONS,              // TAKE_MEDIA_BUTTONS
            OP_TAKE_AUDIO_FOCUS,                // TAKE_AUDIO_FOCUS
            OP_AUDIO_MASTER_VOLUME,             // AUDIO_MASTER_VOLUME
            OP_AUDIO_VOICE_VOLUME,              // AUDIO_VOICE_VOLUME
            OP_AUDIO_RING_VOLUME,               // AUDIO_RING_VOLUME
            OP_AUDIO_MEDIA_VOLUME,              // AUDIO_MEDIA_VOLUME
            OP_AUDIO_ALARM_VOLUME,              // AUDIO_ALARM_VOLUME
            OP_AUDIO_NOTIFICATION_VOLUME,       // AUDIO_NOTIFICATION_VOLUME
            OP_AUDIO_BLUETOOTH_VOLUME,          // AUDIO_BLUETOOTH_VOLUME
            OP_WAKE_LOCK,                       // WAKE_LOCK
            OP_COARSE_LOCATION,                 // MONITOR_LOCATION
            OP_COARSE_LOCATION,                 // MONITOR_HIGH_POWER_LOCATION
            OP_GET_USAGE_STATS,                 // GET_USAGE_STATS
            OP_MUTE_MICROPHONE,                 // MUTE_MICROPHONE
            OP_TOAST_WINDOW,                    // TOAST_WINDOW
            OP_PROJECT_MEDIA,                   // PROJECT_MEDIA
            OP_ACTIVATE_VPN,                    // ACTIVATE_VPN
            OP_WRITE_WALLPAPER,                 // WRITE_WALLPAPER
            OP_ASSIST_STRUCTURE,                // ASSIST_STRUCTURE
            OP_ASSIST_SCREENSHOT,               // ASSIST_SCREENSHOT
            OP_READ_PHONE_STATE,                // READ_PHONE_STATE
            OP_ADD_VOICEMAIL,                   // ADD_VOICEMAIL
            OP_USE_SIP,                         // USE_SIP
            OP_PROCESS_OUTGOING_CALLS,          // PROCESS_OUTGOING_CALLS
            OP_USE_FINGERPRINT,                 // USE_FINGERPRINT
            OP_BODY_SENSORS,                    // BODY_SENSORS
            OP_READ_CELL_BROADCASTS,            // READ_CELL_BROADCASTS
            OP_MOCK_LOCATION,                   // MOCK_LOCATION
            OP_READ_EXTERNAL_STORAGE,           // READ_EXTERNAL_STORAGE
            OP_WRITE_EXTERNAL_STORAGE,          // WRITE_EXTERNAL_STORAGE
            OP_TURN_SCREEN_ON,                  // TURN_SCREEN_ON
            OP_GET_ACCOUNTS,                    // GET_ACCOUNTS
            OP_RUN_IN_BACKGROUND,               // RUN_IN_BACKGROUND
            OP_AUDIO_ACCESSIBILITY_VOLUME,      // AUDIO_ACCESSIBILITY_VOLUME
            OP_READ_PHONE_NUMBERS,              // READ_PHONE_NUMBERS
            OP_REQUEST_INSTALL_PACKAGES,        // REQUEST_INSTALL_PACKAGES
            OP_PICTURE_IN_PICTURE,              // ENTER_PICTURE_IN_PICTURE_ON_HIDE
            OP_INSTANT_APP_START_FOREGROUND,    // INSTANT_APP_START_FOREGROUND
            OP_ANSWER_PHONE_CALLS,              // ANSWER_PHONE_CALLS
            OP_RUN_ANY_IN_BACKGROUND,           // OP_RUN_ANY_IN_BACKGROUND
            OP_CHANGE_WIFI_STATE,               // OP_CHANGE_WIFI_STATE
            OP_REQUEST_DELETE_PACKAGES,         // OP_REQUEST_DELETE_PACKAGES
            OP_BIND_ACCESSIBILITY_SERVICE,      // OP_BIND_ACCESSIBILITY_SERVICE
            OP_ACCEPT_HANDOVER,                 // ACCEPT_HANDOVER
            OP_MANAGE_IPSEC_TUNNELS,            // MANAGE_IPSEC_HANDOVERS
            OP_START_FOREGROUND,                // START_FOREGROUND
            OP_COARSE_LOCATION,                 // BLUETOOTH_SCAN
            OP_USE_BIOMETRIC,                   // BIOMETRIC
            OP_ACTIVITY_RECOGNITION,            // ACTIVITY_RECOGNITION
            OP_SMS_FINANCIAL_TRANSACTIONS,      // SMS_FINANCIAL_TRANSACTIONS
            OP_READ_MEDIA_AUDIO,                // READ_MEDIA_AUDIO
            OP_WRITE_MEDIA_AUDIO,               // WRITE_MEDIA_AUDIO
            OP_READ_MEDIA_VIDEO,                // READ_MEDIA_VIDEO
            OP_WRITE_MEDIA_VIDEO,               // WRITE_MEDIA_VIDEO
            OP_READ_MEDIA_IMAGES,               // READ_MEDIA_IMAGES
            OP_WRITE_MEDIA_IMAGES,              // WRITE_MEDIA_IMAGES
            OP_LEGACY_STORAGE,                  // LEGACY_STORAGE
            OP_ACCESS_ACCESSIBILITY,            // ACCESS_ACCESSIBILITY
            OP_READ_DEVICE_IDENTIFIERS,         // READ_DEVICE_IDENTIFIERS
            OP_ACCESS_MEDIA_LOCATION,           // ACCESS_MEDIA_LOCATION
            OP_ACTIVATE_PLATFORM_VPN,           // ACTIVATE_PLATFORM_VPN
    };

    /**
     * This provides a simple name for each operation to be used
     * in debug output.
     */
    private static String[] _sOpNames = new String[] {
            "COARSE_LOCATION",
            "FINE_LOCATION",
            "GPS",
            "VIBRATE",
            "READ_CONTACTS",
            "WRITE_CONTACTS",
            "READ_CALL_LOG",
            "WRITE_CALL_LOG",
            "READ_CALENDAR",
            "WRITE_CALENDAR",
            "WIFI_SCAN",
            "POST_NOTIFICATION",
            "NEIGHBORING_CELLS",
            "CALL_PHONE",
            "READ_SMS",
            "WRITE_SMS",
            "RECEIVE_SMS",
            "RECEIVE_EMERGECY_SMS",
            "RECEIVE_MMS",
            "RECEIVE_WAP_PUSH",
            "SEND_SMS",
            "READ_ICC_SMS",
            "WRITE_ICC_SMS",
            "WRITE_SETTINGS",
            "SYSTEM_ALERT_WINDOW",
            "ACCESS_NOTIFICATIONS",
            "CAMERA",
            "RECORD_AUDIO",
            "PLAY_AUDIO",
            "READ_CLIPBOARD",
            "WRITE_CLIPBOARD",
            "TAKE_MEDIA_BUTTONS",
            "TAKE_AUDIO_FOCUS",
            "AUDIO_MASTER_VOLUME",
            "AUDIO_VOICE_VOLUME",
            "AUDIO_RING_VOLUME",
            "AUDIO_MEDIA_VOLUME",
            "AUDIO_ALARM_VOLUME",
            "AUDIO_NOTIFICATION_VOLUME",
            "AUDIO_BLUETOOTH_VOLUME",
            "WAKE_LOCK",
            "MONITOR_LOCATION",
            "MONITOR_HIGH_POWER_LOCATION",
            "GET_USAGE_STATS",
            "MUTE_MICROPHONE",
            "TOAST_WINDOW",
            "PROJECT_MEDIA",
            "ACTIVATE_VPN",
            "WRITE_WALLPAPER",
            "ASSIST_STRUCTURE",
            "ASSIST_SCREENSHOT",
            "READ_PHONE_STATE",
            "ADD_VOICEMAIL",
            "USE_SIP",
            "PROCESS_OUTGOING_CALLS",
            "USE_FINGERPRINT",
            "BODY_SENSORS",
            "READ_CELL_BROADCASTS",
            "MOCK_LOCATION",
            "READ_EXTERNAL_STORAGE",
            "WRITE_EXTERNAL_STORAGE",
            "TURN_ON_SCREEN",
            "GET_ACCOUNTS",
            "RUN_IN_BACKGROUND",
            "AUDIO_ACCESSIBILITY_VOLUME",
            "READ_PHONE_NUMBERS",
            "REQUEST_INSTALL_PACKAGES",
            "PICTURE_IN_PICTURE",
            "INSTANT_APP_START_FOREGROUND",
            "ANSWER_PHONE_CALLS",
            "RUN_ANY_IN_BACKGROUND",
            "CHANGE_WIFI_STATE",
            "REQUEST_DELETE_PACKAGES",
            "BIND_ACCESSIBILITY_SERVICE",
            "ACCEPT_HANDOVER",
            "MANAGE_IPSEC_TUNNELS",
            "START_FOREGROUND",
            "BLUETOOTH_SCAN",
            "USE_BIOMETRIC",
            "ACTIVITY_RECOGNITION",
            "SMS_FINANCIAL_TRANSACTIONS",
            "READ_MEDIA_AUDIO",
            "WRITE_MEDIA_AUDIO",
            "READ_MEDIA_VIDEO",
            "WRITE_MEDIA_VIDEO",
            "READ_MEDIA_IMAGES",
            "WRITE_MEDIA_IMAGES",
            "LEGACY_STORAGE",
            "ACCESS_ACCESSIBILITY",
            "READ_DEVICE_IDENTIFIERS",
            "ACCESS_MEDIA_LOCATION",
            "ACTIVATE_PLATFORM_VPN"
    };

    /**
     * This optionally maps a permission to an operation.  If there
     * is no permission associated with an operation, it is null.
     */
    private static String[] _sOpPerms = new String[] {
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            null,
            android.Manifest.permission.VIBRATE,
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.WRITE_CONTACTS,
            android.Manifest.permission.READ_CALL_LOG,
            android.Manifest.permission.WRITE_CALL_LOG,
            android.Manifest.permission.READ_CALENDAR,
            android.Manifest.permission.WRITE_CALENDAR,
            android.Manifest.permission.ACCESS_WIFI_STATE,
            null, // no permission required for notifications
            null, // neighboring cells shares the coarse location perm
            android.Manifest.permission.CALL_PHONE,
            android.Manifest.permission.READ_SMS,
            null, // no permission required for writing sms
            android.Manifest.permission.RECEIVE_SMS,
            "android.permission.RECEIVE_EMERGENCY_BROADCAST", // FIXME: android.Manifest.permission.RECEIVE_EMERGENCY_BROADCAST,
            android.Manifest.permission.RECEIVE_MMS,
            android.Manifest.permission.RECEIVE_WAP_PUSH,
            android.Manifest.permission.SEND_SMS,
            android.Manifest.permission.READ_SMS,
            null, // no permission required for writing icc sms
            android.Manifest.permission.WRITE_SETTINGS,
            android.Manifest.permission.SYSTEM_ALERT_WINDOW,
            "android.permission.ACCESS_NOTIFICATIONS", // FIXME: android.Manifest.permission.ACCESS_NOTIFICATIONS,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO,
            null, // no permission for playing audio
            null, // no permission for reading clipboard
            null, // no permission for writing clipboard
            null, // no permission for taking media buttons
            null, // no permission for taking audio focus
            null, // no permission for changing master volume
            null, // no permission for changing voice volume
            null, // no permission for changing ring volume
            null, // no permission for changing media volume
            null, // no permission for changing alarm volume
            null, // no permission for changing notification volume
            null, // no permission for changing bluetooth volume
            android.Manifest.permission.WAKE_LOCK,
            null, // no permission for generic location monitoring
            null, // no permission for high power location monitoring
            android.Manifest.permission.PACKAGE_USAGE_STATS,
            null, // no permission for muting/unmuting microphone
            null, // no permission for displaying toasts
            null, // no permission for projecting media
            null, // no permission for activating vpn
            null, // no permission for supporting wallpaper
            null, // no permission for receiving assist structure
            null, // no permission for receiving assist screenshot
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.ADD_VOICEMAIL,
            android.Manifest.permission.USE_SIP,
            android.Manifest.permission.PROCESS_OUTGOING_CALLS,
            android.Manifest.permission.USE_FINGERPRINT,
            android.Manifest.permission.BODY_SENSORS,
            "android.permission.READ_CELL_BROADCASTS", // FIXME: android.Manifest.permission.READ_CELL_BROADCASTS,
            null,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            null, // no permission for turning the screen on
            android.Manifest.permission.GET_ACCOUNTS,
            null, // no permission for running in background
            null, // no permission for changing accessibility volume
            "android.permission.READ_PHONE_NUMBERS", // FIXME: android.Manifest.permission.READ_PHONE_NUMBERS,
            android.Manifest.permission.REQUEST_INSTALL_PACKAGES,
            null, // no permission for entering picture-in-picture on hide
            "android.permission.INSTANT_APP_FOREGROUND_SERVICE", // FIXME: android.Manifest.permission.INSTANT_APP_FOREGROUND_SERVICE,
            "android.permission.ANSWER_PHONE_CALLS", // FIXME: android.Manifest.permission.ANSWER_PHONE_CALLS,
            null, // no permission for OP_RUN_ANY_IN_BACKGROUND
            android.Manifest.permission.CHANGE_WIFI_STATE,
            "android.permission.REQUEST_DELETE_PACKAGES", // FIXME: android.Manifest.permission.REQUEST_DELETE_PACKAGES,
            android.Manifest.permission.BIND_ACCESSIBILITY_SERVICE,
            "android.permission.ACCEPT_HANDOVER", // FIXME: android.Manifest.permission.ACCEPT_HANDOVER,
            null, // no permission for OP_MANAGE_IPSEC_TUNNELS
            "android.permission.FOREGROUND_SERVICE", // FIXME: android.Manifest.permission.FOREGROUND_SERVICE,
            null, // no permission for OP_BLUETOOTH_SCAN
            "android.permission.USE_BIOMETRIC", // FIXME: android.Manifest.permission.USE_BIOMETRIC,
            "android.permission.ACTIVITY_RECOGNITION", // FIXME: android.Manifest.permission.ACTIVITY_RECOGNITION,
            "android.permission.SMS_FINANCIAL_TRANSACTIONS", // FIXME: android.Manifest.permission.SMS_FINANCIAL_TRANSACTIONS,
            null,
            null, // no permission for OP_WRITE_MEDIA_AUDIO
            null,
            null, // no permission for OP_WRITE_MEDIA_VIDEO
            null,
            null, // no permission for OP_WRITE_MEDIA_IMAGES
            null, // no permission for OP_LEGACY_STORAGE
            null, // no permission for OP_ACCESS_ACCESSIBILITY
            null, // no direct permission for OP_READ_DEVICE_IDENTIFIERS
            "android.permission.ACCESS_MEDIA_LOCATION", // FIXME: android.Manifest.permission.ACCESS_MEDIA_LOCATION,
            null, // no permission for OP_ACTIVATE_PLATFORM_VPN
    };

    // Reference: https://developer.android.com/reference/android/Manifest.permission
    // Reference: rikka.appops
    // TODO: Move from hardcoding to settings
    private static Integer[] _ALWAYS_SHOWN_OP = {
            // [Location]
            OP_COARSE_LOCATION,
            // OP_MOCK_LOCATION,
            // OP_ACCESS_MEDIA_LOCATION,
            // [Storage]
            OP_READ_EXTERNAL_STORAGE,
            OP_WRITE_EXTERNAL_STORAGE,
            // OP_LEGACY_STORAGE,
            // [Calendar]
            OP_READ_CALENDAR,
            OP_WRITE_CALENDAR,
            // [Calling]
            OP_CALL_PHONE,
            OP_PROCESS_OUTGOING_CALLS,
            OP_READ_CALL_LOG,
            OP_ANSWER_PHONE_CALLS,
            OP_WRITE_CALL_LOG,
            OP_ADD_VOICEMAIL,
            OP_USE_SIP,
            OP_ACCEPT_HANDOVER,
            // [Phone]
            OP_READ_PHONE_STATE,
            OP_READ_PHONE_NUMBERS,
            OP_READ_DEVICE_IDENTIFIERS,
            OP_GET_USAGE_STATS,
            OP_GET_ACCOUNTS,
            // [Camera]
            OP_CAMERA,
            // [SMS]
            OP_READ_SMS,
            OP_WRITE_SMS,
            OP_RECEIVE_SMS,
            OP_RECEIVE_MMS,
            OP_RECEIVE_WAP_PUSH,
            OP_SEND_SMS,
            OP_SMS_FINANCIAL_TRANSACTIONS,
            OP_READ_CELL_BROADCASTS,
            // [Module]
            OP_BODY_SENSORS,
            OP_ACTIVITY_RECOGNITION,
            OP_USE_FINGERPRINT,
            OP_USE_BIOMETRIC,
            // [Contact]
            OP_READ_CONTACTS,
            OP_WRITE_CONTACTS,
            // [Microphone]
            OP_RECORD_AUDIO,
            // OP_MUTE_MICROPHONE,
            // [Audio]
            // OP_PLAY_AUDIO,
            // OP_PROJECT_MEDIA,
            // OP_TAKE_MEDIA_BUTTONS,
            // OP_TAKE_AUDIO_FOCUS,
            // OP_AUDIO_MASTER_VOLUME,
            // OP_AUDIO_VOICE_VOLUME,
            // OP_AUDIO_RING_VOLUME,
            // OP_AUDIO_MEDIA_VOLUME,
            // OP_AUDIO_ALARM_VOLUME,
            // OP_AUDIO_NOTIFICATION_VOLUME,
            // OP_AUDIO_BLUETOOTH_VOLUME,
            // OP_AUDIO_ACCESSIBILITY_VOLUME,
            // OP_READ_MEDIA_AUDIO,
            // OP_WRITE_MEDIA_AUDIO,
            // OP_READ_MEDIA_VIDEO,
            // OP_WRITE_MEDIA_VIDEO,
            // OP_READ_MEDIA_IMAGES,
            // OP_WRITE_MEDIA_IMAGES,
            // [Settings]
            OP_WRITE_SETTINGS,
            OP_WAKE_LOCK,
            OP_TURN_SCREEN_ON,
            OP_REQUEST_INSTALL_PACKAGES,
            OP_REQUEST_DELETE_PACKAGES,
            OP_CHANGE_WIFI_STATE,
            // OP_WRITE_WALLPAPER,
            // OP_ACTIVATE_VPN,
            // OP_ACTIVATE_PLATFORM_VPN,
            // [Accessibility]
            OP_BIND_ACCESSIBILITY_SERVICE,
            OP_ACCESS_ACCESSIBILITY,
            // OP_ASSIST_STRUCTURE,
            // OP_ASSIST_SCREENSHOT,
            // [Service]
            OP_RUN_IN_BACKGROUND,
            OP_RUN_ANY_IN_BACKGROUND,
            OP_INSTANT_APP_START_FOREGROUND,
            OP_START_FOREGROUND,
            // [Other]
            // OP_POST_NOTIFICATION,
            OP_SYSTEM_ALERT_WINDOW,
            OP_ACCESS_NOTIFICATIONS,
            OP_READ_CLIPBOARD,
            // OP_VIBRATE,
            OP_WRITE_CLIPBOARD,
            OP_TOAST_WINDOW,
            OP_PICTURE_IN_PICTURE,
            OP_MANAGE_IPSEC_TUNNELS,
    };

    private static String[] _PERMISSION_GROUP_ORDER = {
            CustomPermissionGroup.CAMERA,
            CustomPermissionGroup.MICROPHONE,
            CustomPermissionGroup.STORAGE,
            CustomPermissionGroup.CALENDAR,
            CustomPermissionGroup.LOCATION,
            CustomPermissionGroup.SMS,
            CustomPermissionGroup.CALLING,
            CustomPermissionGroup.CONTACT,
            CustomPermissionGroup.PHONE,
            CustomPermissionGroup.SETTINGS,
            CustomPermissionGroup.ACCESSIBILITY,
            CustomPermissionGroup.MODULE,
            CustomPermissionGroup.AUDIO,
            CustomPermissionGroup.OTHER,
            CustomPermissionGroup.SERVICE,
    };

    private static Map<Integer, String> _OP_CUSTOM_PERMISSION_GROUP_MAP = new HashMap<Integer, String>() {{
        put(OP_COARSE_LOCATION, CustomPermissionGroup.LOCATION);
        put(OP_FINE_LOCATION, CustomPermissionGroup.LOCATION);
        put(OP_GPS, CustomPermissionGroup.LOCATION);
        put(OP_VIBRATE, CustomPermissionGroup.OTHER);
        put(OP_READ_CONTACTS, CustomPermissionGroup.CONTACT);
        put(OP_WRITE_CONTACTS, CustomPermissionGroup.CONTACT);
        put(OP_READ_CALL_LOG, CustomPermissionGroup.CALLING);
        put(OP_WRITE_CALL_LOG, CustomPermissionGroup.CALLING);
        put(OP_READ_CALENDAR, CustomPermissionGroup.CALENDAR);
        put(OP_WRITE_CALENDAR, CustomPermissionGroup.CALENDAR);
        put(OP_WIFI_SCAN, CustomPermissionGroup.LOCATION);
        put(OP_POST_NOTIFICATION, CustomPermissionGroup.OTHER);
        put(OP_NEIGHBORING_CELLS, CustomPermissionGroup.LOCATION);
        put(OP_CALL_PHONE, CustomPermissionGroup.CALLING);
        put(OP_READ_SMS, CustomPermissionGroup.SMS);
        put(OP_WRITE_SMS, CustomPermissionGroup.SMS);
        put(OP_RECEIVE_SMS, CustomPermissionGroup.SMS);
        put(OP_RECEIVE_EMERGECY_SMS, CustomPermissionGroup.SMS);
        put(OP_RECEIVE_MMS, CustomPermissionGroup.SMS);
        put(OP_RECEIVE_WAP_PUSH, CustomPermissionGroup.SMS);
        put(OP_SEND_SMS, CustomPermissionGroup.SMS);
        put(OP_READ_ICC_SMS, CustomPermissionGroup.SMS);
        put(OP_WRITE_ICC_SMS, CustomPermissionGroup.SMS);
        put(OP_WRITE_SETTINGS, CustomPermissionGroup.SETTINGS);
        put(OP_SYSTEM_ALERT_WINDOW, CustomPermissionGroup.OTHER);
        put(OP_ACCESS_NOTIFICATIONS, CustomPermissionGroup.OTHER);
        put(OP_CAMERA, CustomPermissionGroup.CAMERA);
        put(OP_RECORD_AUDIO, CustomPermissionGroup.MICROPHONE);
        put(OP_PLAY_AUDIO, CustomPermissionGroup.AUDIO);
        put(OP_READ_CLIPBOARD, CustomPermissionGroup.OTHER);
        put(OP_WRITE_CLIPBOARD, CustomPermissionGroup.OTHER);
        put(OP_TAKE_MEDIA_BUTTONS, CustomPermissionGroup.AUDIO);
        put(OP_TAKE_AUDIO_FOCUS, CustomPermissionGroup.AUDIO);
        put(OP_AUDIO_MASTER_VOLUME, CustomPermissionGroup.AUDIO);
        put(OP_AUDIO_VOICE_VOLUME, CustomPermissionGroup.AUDIO);
        put(OP_AUDIO_RING_VOLUME, CustomPermissionGroup.AUDIO);
        put(OP_AUDIO_MEDIA_VOLUME, CustomPermissionGroup.AUDIO);
        put(OP_AUDIO_ALARM_VOLUME, CustomPermissionGroup.AUDIO);
        put(OP_AUDIO_NOTIFICATION_VOLUME, CustomPermissionGroup.AUDIO);
        put(OP_AUDIO_BLUETOOTH_VOLUME, CustomPermissionGroup.AUDIO);
        put(OP_WAKE_LOCK, CustomPermissionGroup.SETTINGS);
        put(OP_MONITOR_LOCATION, CustomPermissionGroup.LOCATION);
        put(OP_MONITOR_HIGH_POWER_LOCATION, CustomPermissionGroup.LOCATION);
        put(OP_GET_USAGE_STATS, CustomPermissionGroup.PHONE);
        put(OP_MUTE_MICROPHONE, CustomPermissionGroup.MICROPHONE);
        put(OP_TOAST_WINDOW, CustomPermissionGroup.OTHER);
        put(OP_PROJECT_MEDIA, CustomPermissionGroup.AUDIO);
        put(OP_ACTIVATE_VPN, CustomPermissionGroup.SETTINGS);
        put(OP_WRITE_WALLPAPER, CustomPermissionGroup.SETTINGS);
        put(OP_ASSIST_STRUCTURE, CustomPermissionGroup.ACCESSIBILITY);
        put(OP_ASSIST_SCREENSHOT, CustomPermissionGroup.ACCESSIBILITY);
        put(OP_READ_PHONE_STATE, CustomPermissionGroup.PHONE);
        put(OP_ADD_VOICEMAIL, CustomPermissionGroup.CALLING);
        put(OP_USE_SIP, CustomPermissionGroup.CALLING);
        put(OP_PROCESS_OUTGOING_CALLS, CustomPermissionGroup.CALLING);
        put(OP_USE_FINGERPRINT, CustomPermissionGroup.MODULE);
        put(OP_BODY_SENSORS, CustomPermissionGroup.MODULE);
        put(OP_READ_CELL_BROADCASTS, CustomPermissionGroup.SMS);
        put(OP_MOCK_LOCATION, CustomPermissionGroup.LOCATION);
        put(OP_READ_EXTERNAL_STORAGE, CustomPermissionGroup.STORAGE);
        put(OP_WRITE_EXTERNAL_STORAGE, CustomPermissionGroup.STORAGE);
        put(OP_TURN_SCREEN_ON, CustomPermissionGroup.SETTINGS);
        put(OP_GET_ACCOUNTS, CustomPermissionGroup.PHONE);
        put(OP_RUN_IN_BACKGROUND, CustomPermissionGroup.SERVICE);
        put(OP_AUDIO_ACCESSIBILITY_VOLUME, CustomPermissionGroup.AUDIO);
        put(OP_READ_PHONE_NUMBERS, CustomPermissionGroup.PHONE);
        put(OP_REQUEST_INSTALL_PACKAGES, CustomPermissionGroup.SETTINGS);
        put(OP_PICTURE_IN_PICTURE, CustomPermissionGroup.OTHER);
        put(OP_INSTANT_APP_START_FOREGROUND, CustomPermissionGroup.SERVICE);
        put(OP_ANSWER_PHONE_CALLS, CustomPermissionGroup.CALLING);
        put(OP_RUN_ANY_IN_BACKGROUND, CustomPermissionGroup.SERVICE);
        put(OP_CHANGE_WIFI_STATE, CustomPermissionGroup.SETTINGS);
        put(OP_REQUEST_DELETE_PACKAGES, CustomPermissionGroup.SETTINGS);
        put(OP_BIND_ACCESSIBILITY_SERVICE, CustomPermissionGroup.ACCESSIBILITY);
        put(OP_ACCEPT_HANDOVER, CustomPermissionGroup.CALLING);
        put(OP_MANAGE_IPSEC_TUNNELS, CustomPermissionGroup.OTHER);
        put(OP_START_FOREGROUND, CustomPermissionGroup.SERVICE);
        put(OP_BLUETOOTH_SCAN, CustomPermissionGroup.LOCATION);
        put(OP_USE_BIOMETRIC, CustomPermissionGroup.MODULE);
        put(OP_ACTIVITY_RECOGNITION, CustomPermissionGroup.MODULE);
        put(OP_SMS_FINANCIAL_TRANSACTIONS, CustomPermissionGroup.SMS);
        put(OP_READ_MEDIA_AUDIO, CustomPermissionGroup.AUDIO);
        put(OP_WRITE_MEDIA_AUDIO, CustomPermissionGroup.AUDIO);
        put(OP_READ_MEDIA_VIDEO, CustomPermissionGroup.AUDIO);
        put(OP_WRITE_MEDIA_VIDEO, CustomPermissionGroup.AUDIO);
        put(OP_READ_MEDIA_IMAGES, CustomPermissionGroup.AUDIO);
        put(OP_WRITE_MEDIA_IMAGES, CustomPermissionGroup.AUDIO);
        put(OP_LEGACY_STORAGE, CustomPermissionGroup.STORAGE);
        put(OP_ACCESS_ACCESSIBILITY, CustomPermissionGroup.ACCESSIBILITY);
        put(OP_READ_DEVICE_IDENTIFIERS, CustomPermissionGroup.PHONE);
        put(OP_ACCESS_MEDIA_LOCATION, CustomPermissionGroup.LOCATION);
        put(OP_ACTIVATE_PLATFORM_VPN, CustomPermissionGroup.SETTINGS);
    }};

    public static final class CustomPermissionGroup {
        public static final String LOCATION = "com.zzzmode.appopsx.permission-group.LOCATION";
        public static final String STORAGE = "com.zzzmode.appopsx.permission-group.STORAGE";
        public static final String CALENDAR = "com.zzzmode.appopsx.permission-group.CALENDAR";
        public static final String CALLING = "com.zzzmode.appopsx.permission-group.CALLING";
        public static final String PHONE = "com.zzzmode.appopsx.permission-group.PHONE";
        public static final String CAMERA = "com.zzzmode.appopsx.permission-group.CAMERA";
        public static final String SMS = "com.zzzmode.appopsx.permission-group.SMS";
        public static final String MODULE = "com.zzzmode.appopsx.permission-group.MODULE";
        public static final String CONTACT = "com.zzzmode.appopsx.permission-group.CONTACT";
        public static final String MICROPHONE = "com.zzzmode.appopsx.permission-group.MICROPHONE";
        public static final String AUDIO = "com.zzzmode.appopsx.permission-group.AUDIO";
        public static final String SETTINGS = "com.zzzmode.appopsx.permission-group.SETTINGS";
        public static final String ACCESSIBILITY = "com.zzzmode.appopsx.permission-group.ACCESSIBILITY";
        public static final String SERVICE = "com.zzzmode.appopsx.permission-group.SERVICE";
        public static final String OTHER = "com.zzzmode.appopsx.permission-group.OTHER";
    }

    static {
        sOpToSwitch = Collections.unmodifiableList(Arrays.asList(_sOpToSwitch));
        sOpNames = Collections.unmodifiableList(Arrays.asList(_sOpNames));
        sOpPerms = Collections.unmodifiableList(Arrays.asList(_sOpPerms));
        ALWAYS_SHOWN_OP = Collections.unmodifiableList(Arrays.asList(_ALWAYS_SHOWN_OP));
        PERMISSION_GROUP_ORDER = Collections.unmodifiableList(Arrays.asList(_PERMISSION_GROUP_ORDER));
        OP_CUSTOM_PERMISSION_GROUP_MAP = Collections.unmodifiableMap(_OP_CUSTOM_PERMISSION_GROUP_MAP);
    }
}
