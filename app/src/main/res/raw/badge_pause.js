var event = getEvent();
var item = event.getItem();
var service = item.my.connection.service;
if (service != null) {
    try {
        service.unregisterListener(item.my.connection.listener);
        var conn = item.my.connection.conn;
        if(conn != null){
            getActiveScreen().getContext().unbindService(conn);
        }
    } catch (e) {}
}