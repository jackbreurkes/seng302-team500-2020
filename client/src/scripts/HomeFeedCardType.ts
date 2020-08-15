export interface HomeFeedCardType {
    change_id: number,
    entity_type: string,
    entity_id: number,
    entity_name: string,
    creator_id: number,
    creator_name: string,
    edited_timestamp: string,
    editor_id: number,
    editor_name: string,
    changed_attribute: string,
    old_value: string,
    new_value: JSON
}