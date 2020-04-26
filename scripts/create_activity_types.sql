/* MariaDB script to populate the activity_types table with some default activity types.
 * should be run on the database where the table is defined.
 * TODO potentially add this to the build pipeline to have it run automatically.
 * @author Jack van Heugten Breurkes
 */


/* IGNORE will skip inserting already existing entries */
INSERT IGNORE INTO `activity_type` (`activity_type_id`, `activity_type_name`)
VALUES
    (NULL, 'Walking'),  /* NULL id will insert the next available auto-incremented ID */
    (NULL, 'Running'),
    (NULL, 'Swimming'),
    (NULL, 'Road Biking'),
    (NULL, 'Mountain Biking'),
    (NULL, 'Scootering'),
    (NULL, 'Skateboarding');
