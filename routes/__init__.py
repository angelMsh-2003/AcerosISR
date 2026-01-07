# routes/__init__.py
from .empleados import empleados_bp
from .tareas import tareas_bp
from .proyectos import proyectos_bp
from .materiales import materiales_bp
from .actual_user import actual_user_bp

def register_routes(app):
    app.register_blueprint(empleados_bp, url_prefix="/api")
    app.register_blueprint(tareas_bp, url_prefix="/api")
    app.register_blueprint(proyectos_bp, url_prefix="/api")
    app.register_blueprint(materiales_bp, url_prefix="/api")
    app.register_blueprint(actual_user_bp, url_prefix="/api")