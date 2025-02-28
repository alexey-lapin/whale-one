import { errorToast, successToast } from '@/utils/toasts.ts'
import { apiClient, apiClientContext } from '@/client/baseClient.ts'
import type EquipmentTypeAttributeModel from '@/model/EquipmentTypeAttributeModel.ts'

export type AttributeEntity = 'equipment' | 'deployment'

export const invokeAttributeCreateOrUpdate = (
  attributeEntity: AttributeEntity,
  attribute: EquipmentTypeAttributeModel,
) => {
  if (attribute.id > 0) {
    return invokeAttributeUpdate(attributeEntity, attribute)
  } else {
    return invokeAttributeCreate(attributeEntity, attribute)
  }
}

export const invokeAttributeCreate = (
  attributeEntity: AttributeEntity,
  attribute: EquipmentTypeAttributeModel,
) => {
  return apiClient
    .post<EquipmentTypeAttributeModel>(
      `/api/equipment/types/${attribute.equipmentTypeId}/attributes/${attributeEntity}`,
      attribute,
    )
    .then((response) => {
      const data = response.data
      apiClientContext.toast?.add(
        successToast(`Attribute #${data.id} ${data.name} has been created`),
      )
      return data
    })
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error.message))
      throw error
    })
}

export const invokeAttributeUpdate = (
  attributeEntity: AttributeEntity,
  attribute: EquipmentTypeAttributeModel,
) => {
  return apiClient
    .put<EquipmentTypeAttributeModel>(
      `/api/equipment/types/${attribute.equipmentTypeId}/attributes/${attributeEntity}/${attribute.id}`,
      attribute,
    )
    .then((response) => {
      const data = response.data
      apiClientContext.toast?.add(
        successToast(`Attribute #${data.id} ${data.name} has been updated`),
      )
    })
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error.message))
      throw error
    })
}

export const invokeAttributeDelete = (
  attributeEntity: AttributeEntity,
  attribute: EquipmentTypeAttributeModel,
) => {
  return apiClient
    .delete(
      `/api/equipment/types/${attribute.equipmentTypeId}/attributes/${attributeEntity}/${attribute.id}`,
    )
    .then((data) => {
      apiClientContext.toast?.add(
        successToast(`Attribute #${attribute.id} ${attribute.name} has been deleted`),
      )
      return data
    })
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error.message))
    })
}

export const invokeAttributeListGet = (attributeEntity: AttributeEntity, typeId: number) => {
  return apiClient
    .get<EquipmentTypeAttributeModel[]>(
      `/api/equipment/types/${typeId}/attributes/${attributeEntity}`,
    )
    .then((response) => response.data)
    .catch((error) => {
      apiClientContext.toast?.add(errorToast(error.message))
      throw error
    })
}

// // Deployment Attributes
//
// export const invokeDeploymentAttributeCreateOrUpdate = (attribute: EquipmentTypeAttributeModel) => {
//   if (attribute.id > 0) {
//     return invokeDeploymentAttributeUpdate(attribute)
//   } else {
//     return invokeDeploymentAttributeCreate(attribute)
//   }
// }
//
// export const invokeDeploymentAttributeCreate = (attribute: EquipmentTypeAttributeModel) => {
//   return apiClient
//     .post<EquipmentTypeAttributeModel>(
//       `/api/equipment/types/${attribute.equipmentTypeId}/attributes/deployment`,
//       attribute,
//     )
//     .then((response) => {
//       const data = response.data
//       apiClientContext.toast?.add(
//         successToast(`Attribute #${data.id} ${data.name} has been created`),
//       )
//       return data
//     })
//     .catch((error) => {
//       apiClientContext.toast?.add(errorToast(error.message))
//       throw error
//     })
// }
//
// export const invokeDeploymentAttributeUpdate = (attribute: EquipmentTypeAttributeModel) => {
//   return apiClient
//     .put<EquipmentTypeAttributeModel>(
//       `/api/equipment/types/${attribute.equipmentTypeId}/attributes/deployment/${attribute.id}`,
//       attribute,
//     )
//     .then((response) => {
//       const data = response.data
//       apiClientContext.toast?.add(
//         successToast(`Attribute #${data.id} ${data.name} has been updated`),
//       )
//     })
//     .catch((error) => {
//       apiClientContext.toast?.add(errorToast(error.message))
//       throw error
//     })
// }
//
// export const invokeDeploymentAttributeListGet = (typeId: number) => {
//   return apiClient
//     .get<EquipmentTypeAttributeModel[]>(`/api/equipment/types/${typeId}/attributes/deployment`)
//     .then((response) => response.data)
//     .catch((error) => {
//       apiClientContext.toast?.add(errorToast(error.message))
//       throw error
//     })
// }
