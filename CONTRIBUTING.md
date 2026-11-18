# Guía de trabajo en equipo — SportConnect

Esta guía define cómo preparar el repositorio, desarrollar cambios en equipo y promover versiones desde desarrollo hasta producción. La rama que ya está publicada es `developer`; se usará como rama de integración.

## 1. Crear el repositorio

Quien crea el proyecto:

1. Crea un repositorio en GitHub con el nombre acordado y elige su visibilidad. En un repositorio público, cualquiera puede leer y clonar el código; solo colaboradores autorizados pueden escribir.
2. Si el proyecto ya existe localmente y tiene commits, crea el repositorio vacío en GitHub: no inicialices también un README, `.gitignore` o licencia desde la página, para evitar historiales desconectados.
3. Comprueba que los archivos locales y el `.gitignore` sean apropiados y que no haya credenciales, claves de firma ni archivos locales que deban mantenerse privados.
4. Si el proyecto todavía no tiene un remoto, conecta GitHub y publica la rama de integración:

   ```bash
   git remote add origin https://github.com/<cuenta>/<repositorio>.git
   git push -u origin developer
   ```

   Si `origin` ya existe, comprueba su URL con `git remote -v` en vez de añadirlo de nuevo. En SportConnect, el repositorio ya está creado y `developer` ya fue subida; no es necesario repetir este paso.

## 2. Ramas y propósito

Mantengan pocas ramas permanentes y creen ramas temporales para cada tarea:

| Rama | Propósito | Se actualiza mediante |
| --- | --- | --- |
| `developer` | Integrar funcionalidades y correcciones aprobadas. No representa una versión de producción. | Pull Requests desde ramas de trabajo. |
| `preproduction` | Probar una versión candidata en un entorno de pruebas antes de liberarla. | Pull Request desde `developer` cuando la versión esté lista para QA. |
| `main` | Código aprobado y publicado en producción. | Pull Request desde `preproduction` después de la aprobación de QA. |
| `feature/...` | Desarrollar una funcionalidad o tarea. | Se crea desde `developer`; se integra mediante Pull Request a `developer`. |
| `fix/...` | Corregir un error que aún no está en producción. | Se crea desde `developer`; se integra mediante Pull Request a `developer`. |
| `hotfix/...` | Corregir urgentemente un error de producción. | Se crea desde `main`; se integra a `main` y luego se lleva también a `preproduction` y `developer`. |

No creen `preproduction` ni `main` a partir de código que todavía no haya pasado la revisión necesaria. Hasta que exista una primera versión aprobada, trabajen desde `developer`.

## 3. Preparación inicial del equipo

1. El propietario invita a cada colaborador desde **Settings → Collaborators** en GitHub. Concedan permisos de escritura solo a quienes deban contribuir.
2. Protejan `developer`, `preproduction` y `main` desde **Settings → Branches**:
   - exigir Pull Request antes de integrar;
   - exigir al menos una aprobación de otra persona;
   - exigir que las verificaciones automáticas pasen cuando CI esté configurado;
   - bloquear *force push* y eliminación de las ramas.
3. Configuren `developer` como rama de integración predeterminada mientras el equipo aún no tenga una versión de producción. Al crear `main`, configúrenla como rama predeterminada si ese es el flujo acordado.
4. Cada integrante clona el repositorio y abre el proyecto con Android Studio:

   ```bash
   git clone https://github.com/Mizaki92/SportConnect.git
   cd SportConnect
   git switch developer
   git pull --ff-only origin developer
   ```

5. Cada desarrollador configura su SDK local en `local.properties`. Ese archivo no se comparte ni se sube al repositorio. Tampoco se deben subir claves de firma, contraseñas, tokens ni archivos con credenciales. Para secretos de CI, usen **Settings → Secrets and variables → Actions**.

## 4. Flujo para cada tarea

### 4.1 Crear la tarea

Antes de programar, creen o elijan una incidencia de GitHub con:

- el problema o necesidad;
- el resultado esperado y criterios de aceptación;
- capturas o pasos para reproducirlo, si corresponde;
- una persona responsable y prioridad.

Dividan tareas grandes en partes revisables. Una rama debe resolver una tarea coherente, no acumular cambios no relacionados.

### 4.2 Crear una rama de trabajo

Partan siempre de la última versión de `developer`:

```bash
git switch developer
git pull --ff-only origin developer
git switch -c feature/42-busqueda-canchas
```

Convención para nombres:

- `feature/<id>-<descripcion-corta>` — funcionalidad nueva.
- `fix/<id>-<descripcion-corta>` — error que no está en producción.
- `hotfix/<id>-<descripcion-corta>` — error urgente en producción; se parte de `main`.
- `release/<version>` — opcional, para estabilizar una versión si QA requiere varios días o varias correcciones.

Usen minúsculas, guiones y el ID de la incidencia cuando exista. Ejemplos: `feature/42-busqueda-canchas`, `fix/57-validar-reserva`.

### 4.3 Desarrollar y guardar cambios

- Hagan cambios pequeños y enfocados; no mezclen formato general, refactorizaciones y funcionalidades en el mismo cambio sin necesidad.
- Añadan o actualicen pruebas cuando cambie el comportamiento.
- Para Android, ejecuten antes de abrir un Pull Request:

  ```powershell
  .\gradlew.bat testDebugUnitTest
  .\gradlew.bat assembleDebug
  ```

- Comprueben qué van a incluir antes de preparar el commit:

  ```bash
  git status
  git diff
  git add <archivos-relacionados>
  git diff --cached
  ```

  Añadan explícitamente los archivos pertinentes en vez de incluir indiscriminadamente todo el directorio. Nunca agreguen `local.properties`, archivos de compilación (`build/`) ni credenciales.

### 4.4 Convenciones de commits

Usen mensajes breves y descriptivos con esta forma:

```text
tipo(área): descripción en infinitivo
```

Tipos recomendados:

- `feat`: funcionalidad nueva.
- `fix`: corrección de un error.
- `refactor`: cambio interno sin alterar el comportamiento esperado.
- `test`: pruebas.
- `docs`: documentación.
- `chore`: mantenimiento o dependencias.
- `style`: formato o presentación sin cambio funcional.

Ejemplos:

```text
feat(auth): permitir recuperar la contraseña
fix(reservas): validar el horario seleccionado
test(canchas): cubrir la búsqueda por deporte
docs: explicar el flujo de Pull Requests
```

Cada commit debe representar un cambio lógico. Eviten mensajes como `cambios`, `arreglos` o `final`.

### 4.5 Subir y abrir un Pull Request

Suban la rama y creen un Pull Request cuyo destino sea `developer`:

```bash
git push -u origin feature/42-busqueda-canchas
```

En el Pull Request incluyan:

- qué cambia y por qué;
- el ID de la incidencia (`Closes #42` cuando corresponda);
- las pruebas ejecutadas y su resultado;
- capturas o video para cambios de interfaz;
- cualquier configuración o decisión que el revisor deba conocer.

Soliciten la revisión de al menos otra persona. Atiendan los comentarios y vuelvan a ejecutar las pruebas después de cambios importantes. Integren el Pull Request solo cuando esté aprobado y las verificaciones requeridas pasen. Después de integrarlo, eliminen la rama temporal desde GitHub.

## 5. Promoción entre desarrollo, preproducción y producción

### Desarrollo → preproducción

Promuevan cambios cuando el conjunto de funcionalidades de la versión esté completo, revisado y probado en `developer`. Abran un Pull Request de `developer` a `preproduction`; no copien cambios manualmente.

Al integrar el Pull Request, desplieguen esa versión en el entorno de preproducción. QA debe validar los flujos principales, regresiones y criterios de aceptación. Durante QA, acepten solo correcciones necesarias para liberar; las funcionalidades nuevas vuelven a `developer` y esperan el siguiente ciclo.

Si una corrección requiere estabilización prolongada, pueden crear `release/<version>` desde `developer`, hacer ahí solo correcciones de la versión y luego integrar esas correcciones en las ramas correspondientes mediante Pull Requests.

### Preproducción → producción

Promuevan a producción solo cuando:

1. QA haya aprobado la versión en preproducción;
2. no queden errores bloqueantes conocidos;
3. las pruebas y verificaciones estén aprobadas;
4. el equipo haya identificado la versión que se va a publicar.

Abran un Pull Request de `preproduction` a `main`. Tras su aprobación e integración:

- etiqueten la versión, por ejemplo `v1.2.0`;
- generen el artefacto de Android con el proceso acordado;
- incrementen `versionCode` para cada publicación que Android deba reconocer como nueva;
- desplieguen el artefacto aprobado y registren qué versión se publicó.

No editen directamente `main`; debe reflejar exclusivamente código aprobado para producción.

### Corrección urgente en producción

Para un error crítico, creen `hotfix/<id>-<descripcion>` desde `main`. Revisen y prueben la corrección, y abran un Pull Request hacia `main`. Después de publicarla, integren la misma corrección en `preproduction` y `developer` mediante Pull Requests para que no se pierda en la siguiente versión.

## 6. Actualizar la rama de trabajo y resolver conflictos

Antes de terminar una tarea, incorporen los cambios recientes de `developer`:

```bash
git fetch origin
git rebase origin/developer
```

Si hay conflictos, resuélvanlos archivo por archivo, ejecuten las pruebas y continúen el rebase. No usen `git push --force` en ramas compartidas o protegidas. Si una rama ya fue compartida y el rebase requiere reescribir su historial, coordinen primero con el equipo.

## 7. Reglas de colaboración

- Usen Pull Requests para integrar código; no trabajen todos directamente en `developer`.
- Revisen el código con respeto y expliquen el motivo de los cambios solicitados.
- No suban datos personales, secretos, credenciales ni archivos locales del IDE.
- No mezclen dependencias o cambios de configuración sin explicar su motivo en el Pull Request.
- Informen pronto sobre bloqueos y conflictos; mantengan las ramas temporales pequeñas y de corta duración.
- Si una tarea cambia el comportamiento de la aplicación, actualicen las pruebas y la documentación relacionada.

