package com.shyam.smsspamclassifier

/*Message Data Model*/
class Message{
    var id: Int = 0
    var body : String = ""
    var timestamp : String = ""
    var sender : String = ""
    var label : String = ""

    constructor(body: String, timestamp: String, sender: String){
        this.body = body
        this.timestamp = timestamp
        this.sender = sender
        this.label = label
    }

    constructor(body: String, timestamp: String, sender: String, label: String){
        this.body = body
        this.timestamp = timestamp
        this.sender = sender
        this.label = label
    }

    constructor(id : Int, body: String, timestamp: String, sender: String, label: String){
        this.id = id
        this.body = body
        this.timestamp = timestamp
        this.sender = sender
        this.label = label
    }
}