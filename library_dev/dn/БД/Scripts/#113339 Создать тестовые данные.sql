--- для создания новых данных достаточно в скрипте заменить "${id}" на новое во всех строках по неполному совпадению
--- создание пациента

INSERT INTO f_patient (id, area_id, changed_date, birth_date, update_birth_date) VALUES(${id}, NULL, NULL, NULL, NULL);

--- создание c_dn_justification_check. Не забыть указать тот же dn_document_uid, что и в сообщении

INSERT INTO c_dn_justification_check (dn_document_uid, justification_document_id, simi_received, think_received) VALUES('1${id}', '1${id}', false, false);