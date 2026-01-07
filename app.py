import os
from flask import Flask
from db import db 
def create_app():
    app = Flask(__name__)


    DB_USER = "--"
    DB_PASSWORD = "--"
    DB_NAME = "aceros_isr"
    

    instance_connection_name = os.environ.get("INSTANCE_CONNECTION_NAME")

    if instance_connection_name:
    
        app.config["SQLALCHEMY_DATABASE_URI"] = (
            f"postgresql+psycopg2://{DB_USER}:{DB_PASSWORD}@/{DB_NAME}?host=/cloudsql/{instance_connection_name}"
        )
    else:
    
        DB_HOST = "34.173.25.122" 
        app.config["SQLALCHEMY_DATABASE_URI"] = (
            f"postgresql+psycopg2://{DB_USER}:{DB_PASSWORD}@{DB_HOST}:5432/{DB_NAME}"
        )

    app.config["SQLALCHEMY_TRACK_MODIFICATIONS"] = False

    db.init_app(app)

    from routes.empleados import empleados_bp
    from routes.actual_user import actual_user_bp
    from routes.materiales import materiales_bp
    from routes.proyectos import proyectos_bp
    from routes.tareas import tareas_bp



    app.register_blueprint(empleados_bp, url_prefix="/api")
    app.register_blueprint(actual_user_bp, url_prefix="/api")
    app.register_blueprint(materiales_bp, url_prefix="/api")
    app.register_blueprint(proyectos_bp, url_prefix="/api")
    app.register_blueprint(tareas_bp, url_prefix="/api")

    return app

app = create_app()

if __name__ == "__main__":

    port = int(os.environ.get("PORT", 8080))
    app.run(host="0.0.0.0", port=port, debug=True)



