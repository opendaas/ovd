<?php
// IP/Host of the SessionManager to link the Web Client to
// define('SESSIONMANAGER_HOST', '127.0.0.1');

// Option session: force mode
// define('OPTION_FORCE_SESSION_MODE', 'desktop');
// define('OPTION_FORCE_SESSION_MODE', 'applications');
// default: do not force any behavior

// Option desktop session: force fullscreen mode
// define('OPTION_FORCE_FULLSCREEN', true);
// define('OPTION_FORCE_FULLSCREEN', false);
// default: do not force any behavior

// Option local credentials: show the option
// define('OPTION_SHOW_USE_LOCAL_CREDENTIALS', true);
// define('OPTION_SHOW_USE_LOCAL_CREDENTIALS', false);
// default: do not force any behavior

// Option local credentials: force the mode
// define('OPTION_FORCE_USE_LOCAL_CREDENTIALS', true);
// define('OPTION_FORCE_USE_LOCAL_CREDENTIALS', false);
// default: do not force any behavior

// Enable/disable debug mode
define('DEBUG_MODE', 0);

// Select RDP input method
// define('RDP_INPUT_METHOD', 'scancode'); // default
// define('RDP_INPUT_METHOD', 'unicode');  // alternative method

// Gateway port
//   the port to use to contact the Gateway server in 'gateway_first' mode
//   usefull if the port used by the client to connect to the Gateway is different 
//    from the port binded by the Gateway (nat redirection)
//   default is to use the same port as the client connection
// define('GATEWAY_FORCE_PORT', 443);
