select *
from (
        select t.id, fcot.id cc
        from f_task t
            left join f_continued_obs_task fcot on t.id = fcot.id and t.type_id = 2
     ) tt
where tt.cc is null;

create temporary table tt_doc_id as
select tt.id
from (
        select t.id, fcot.id cc
        from f_task t
            left join f_continued_obs_task fcot on t.id = fcot.id and t.type_id = 2
     ) tt
where tt.cc is null;

delete from f_document_mkb10 where document_id in (select * from tt_doc_id);
delete from f_document where id in (select * from tt_doc_id)