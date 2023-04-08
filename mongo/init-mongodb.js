var db = connect("mongodb://root:root@localhost:27017/admin");

db = db.getSiblingDB('mydb'); // we can not use "use" statement here to switch db

db.createUser(
    {
        user: "mongoadmin",
        pwd: "secret",
        roles: [ { 
            role: "readWrite",
            db: "mydb"
        }]
    }
)

db.user.createIndex( { "email": 1 }, { unique: true } )