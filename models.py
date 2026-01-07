
from db import db
from datetime import datetime

class Empleado(db.Model):
    __tablename__ = "empleados"
    empleado_id = db.Column(db.Integer, primary_key=True)
    num_empleado = db.Column(db.Integer, unique=True, nullable=False)
    nombre = db.Column(db.String(50), nullable=False)
    cargo = db.Column(db.String(20), nullable=False)
    correo = db.Column(db.String(50))
    password_hash = db.Column(db.Text)  
    salt = db.Column(db.Text)
    activo = db.Column(db.Boolean, default=True)

class Proyecto(db.Model):
    __tablename__ = "proyectos"  

    proyecto_id = db.Column(db.Integer, primary_key=True)
    titulo = db.Column(db.Text, nullable=False)            
    descripcion = db.Column(db.Text, nullable=False)
    cliente = db.Column(db.String(100), nullable=False)
    fecha_registro = db.Column(db.DateTime, default=datetime.utcnow)
    registrado_por = db.Column(db.Integer, db.ForeignKey("empleados.empleado_id"))
    estado = db.Column(db.String(20), default="En progreso")  

    def to_dict(self):
        return {
            "proyecto_id": self.proyecto_id,
            "titulo": self.titulo,                       
            "descripcion": self.descripcion,
            "cliente": self.cliente,
            "fecha_registro": self.fecha_registro.isoformat() if self.fecha_registro else None,
            "registrado_por": self.registrado_por,
            "estado": self.estado
        }

class Tarea(db.Model):
    __tablename__ = "tareas"

    tarea_id = db.Column(db.Integer, primary_key=True)
    proyecto_id = db.Column(
        db.Integer, db.ForeignKey("proyectos.proyecto_id"), nullable=False
    )
    empleado_id = db.Column(
        db.Integer, db.ForeignKey("empleados.empleado_id"), nullable=True
    )
    estado = db.Column(db.String(15), default="Pendiente")
    fecha_inicio = db.Column(db.Date)
    fecha_fin = db.Column(db.Date)
    comentarios = db.Column(db.Text)


class Material(db.Model):
    __tablename__ = "materiales"  

    material_id = db.Column(db.Integer, primary_key=True)
    
    tipo = db.Column(
        db.String(20),
        nullable=False
    )  

    nombre = db.Column(
        db.String(50),
        nullable=False,
        unique=False  
    )

    unidad_medida = db.Column(
        db.String(10),
        nullable=False
    )

    descripcion = db.Column(db.Text)

    stock_actual = db.Column(
        db.Numeric(12,2),
        default=0
    )

    def to_dict(self):
        """Para devolver JSON fácilmente"""
        return {
            "material_id": self.material_id,
            "tipo": self.tipo,
            "nombre": self.nombre,
            "unidad_medida": self.unidad_medida,
            "descripcion": self.descripcion,
            "stock_actual": float(self.stock_actual)
        }

class MovimientoMaterial(db.Model):
    __tablename__ = "movimientosmaterial"  

    movimiento_id = db.Column(db.Integer, primary_key=True)

    material_id = db.Column(
        db.Integer,
        db.ForeignKey("materiales.material_id"),  
        nullable=False
    )

    tipo_movimiento = db.Column(db.String(10), nullable=False)  
    fecha = db.Column(db.DateTime, default=datetime.utcnow, nullable=False)
    cantidad = db.Column(db.Numeric(12, 2), nullable=False)
    costo_unitario = db.Column(db.Numeric(12, 2))
    empleado_id = db.Column(db.Integer)  
    observaciones = db.Column(db.Text)

    material = db.relationship("Material", backref="movimientos")


class AgentUser(db.Model):
    __tablename__ = "agentuser"   

    id = db.Column(db.Integer, primary_key=True)

    
    num_empleado = db.Column(
        db.Integer,
        db.ForeignKey("empleados.num_empleado"),
        nullable=False
    )

    nombre = db.Column(db.String(50), nullable=False)
    cargo = db.Column(db.String(20), nullable=False)

    
    ip_address = db.Column(db.String(45))
    device_id = db.Column(db.String(200))
    device_model = db.Column(db.String(200))

    
    fecha_login = db.Column(
        db.DateTime,
        default=datetime.utcnow,
        nullable=False
    )

    fecha_logout = db.Column(db.DateTime)

    empleado = db.relationship("Empleado", backref="sesiones")

class ConsumoMaterial(db.Model):
    __tablename__ = "consumomaterial"

    consumo_id = db.Column(db.Integer, primary_key=True)

    tarea_id = db.Column(
        db.Integer,
        db.ForeignKey("tareas.tarea_id"),
        nullable=False
    )

    material_id = db.Column(
        db.Integer,
        db.ForeignKey("materiales.material_id"),
        nullable=False
    )

    cantidad_usada = db.Column(db.Numeric(12, 2), nullable=False)
    fecha_uso = db.Column(db.DateTime, default=datetime.utcnow, nullable=False)
    observaciones = db.Column(db.Text)

    
    tarea = db.relationship("Tarea", backref="consumos")
    material = db.relationship("Material", backref="consumos")
