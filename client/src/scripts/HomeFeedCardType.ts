export interface HomeFeedCardType {
    entity_id: number,
    entitiy_name: string,
    creator_id: number,
    creator_name: string,
    edited_timestamp: string,
    editor_id: number,
    editor_name: string,
    changed_attribute: string,
    old_value: string,
    new_value: JSON
}