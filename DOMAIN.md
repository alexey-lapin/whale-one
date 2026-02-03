# Domain Description

## Purpose

This system supports underwater acoustic monitoring programs where equipment is deployed to collect recordings
and related observations. It provides a shared, metadata-driven catalog of projects, deployments, equipment,
and analyses so teams can plan operations, track assets, and trace data provenance from collection through
interpretation.

## Guiding principles

- Metadata first: capture who, what, where, when, and why for every event (e.g. deployment, analysis).
- Spatiotemporal grounding: every observation is anchored in time, location, and effort context.
- Traceability: link projects, sites, campaigns, deployments, equipment, analysis, metadata, and results so lineage is
  clear.
- Reusability: define equipment and analysis types once and reuse them across programs.
- Flexibility: allow structured attributes and free-form metadata to accommodate different instruments and studies.

## Modules

- Program and field operations: projects, sites, campaigns, deployments, and recovery planning.
- Equipment management: inventory, condition, maintenance, and assemblies as a distinct asset module.
- Instrument configuration: equipment types, calibration details, and attribute templates.
- Data acquisition and storage: file cataloging, and storage references.
- Access and reporting: users, roles, audit trails, search, and exports.
- [optional/future] Acoustic event catalog: detections, call annotations, and spatiotemporal indexing of events.
- [optional/future] Environmental context: integration of oceanographic or environmental observations and datasets.
- [optional/future] Analysis and results: processing methods, runs, outputs, and quality assessments.

## Business entities

### Project

A long-running program or study with a goal, client or sponsor, region, and type. Projects contain sites, campaigns.
Deployments are linked to projects.

### Site

A named geographic location with coordinates and depth. Sites belong to a project and are used to anchor deployments.

### Campaign

A field operation, usually tied to a vessel or logistical effort, used to carry out deployments and recoveries.
Campaigns belong to a project and can be linked to multiple deployments.

### Deployment

A specific installation of equipment at a site for a defined period. It captures status, platform, providers,
coordinates, timing, and recovery details. Deployments link to equipment assignments and data assets.

### Deployment metadata

Operational telemetry for a deployment, such as sample rate, duty cycle settings, start and stop times, and recording
status. This data supports quality control and downstream analysis.

### Recording

A raw acoustic recording or log captured during a deployment. Recordings are tied to time, location, equipment, and
effort context, and are the primary input to analysis. Currently, recording storage or management is out of scope.

### Equipment type

A definition of an instrument or component class, including whether it is deployable or used as part of an assembly.
Equipment types include attribute definitions and optional metadata for catalogs, manufacturers, and configuration
options.

### Equipment

A physical asset with a unique identity, current status, and type. Equipment can be deployed, stored, maintained, or
retired. It holds values for its type-specific attributes.

### Equipment assembly

A composed system made of multiple equipment items. Assemblies describe how parts fit together and whether the assembly
relationship is temporary for a session or persistent across deployments.

### Equipment attribute

A structured property of an equipment item, derived from its type definition. Attributes can be numeric, textual,
selectable, or file-backed, and may include calibration documents, photos, or configuration files.

### Deployment equipment assignment

The act of associating equipment with a deployment. Assignments may carry deployment-specific attribute values and
determine which assets are active in the field.

### Acoustic event [out of scope]

A detected or observed acoustic signal, such as a call or other event of interest. Events are spatiotemporal and link
back to recordings, equipment, and deployments.

### Annotation [out of scope]

A manual or automated label attached to an event or recording, such as species, call type, confidence, or review status.

### Analysis type

A definition of a processing or interpretation method, with its own configurable attributes and metadata requirements.
Analysis types are reusable across projects and data sets.

### Analysis run [out of scope]

A concrete execution of an analysis type on a data set, capturing parameters, software versions, quality flags, and
outputs. This entity provides provenance for results.

### Data file or data asset

A stored file or external reference that represents raw recordings, logs, or derived products. Data assets are linked to
deployments, equipment, and analysis runs.


### User and role

A system account with permissions and audit identity. User roles define who can create, modify, or approve records.

## Core relationships and rules

- Every deployment belongs to a project and is associated with a site and one or more campaigns.
- Equipment items are instances of equipment types and inherit their attribute definitions.
- An equipment item can be part of only one assembly at a time, and assemblies declare required component types.
- Deployments record a lifecycle that moves from planning to deployment to recovery or cancellation.
- Deployment metadata and recordings should be linked to a deployment to preserve context.
- Acoustic events link to recordings and inherit their spatiotemporal context.
- Environmental observations can be linked to deployments, recordings, or events by time and location.
- Analysis runs depend on a defined analysis type and should record inputs, parameters, and outputs.
- Changes to key records should be attributable to a user and time.

## Typical workflows

- Define a project and its sites.
- Register equipment types and attribute definitions.
- Add equipment assets and, when needed, create assemblies.
- Plan a deployment, assign equipment, and mark it as deployed.
- Capture operational metadata and ingest recordings or logs.
- Detect and annotate acoustic events and link environmental context.
- Recover equipment and update deployment status and recovery details.
- Run analyses and record results, then report or export summaries.

## Boundaries and integrations

- The system may integrate with storage services for large data files and with GIS tools for mapping.
- It should interoperate with analysis pipelines and external registries without forcing a specific technology stack.
