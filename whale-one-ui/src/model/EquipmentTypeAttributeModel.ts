export default interface EquipmentTypeAttributeModel {
  id: number
  version: number
  equipmentTypeId: number
  order: number
  name: string
  description: string
  type: 'number' | 'select' | 'text' | 'textarea'
  metadata?: Record<string, unknown>
}
