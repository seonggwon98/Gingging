package com.c206.backend.domain.quest.dto.response;

import com.c206.backend.domain.member.entity.Member;
import com.c206.backend.domain.pet.entity.Pet;
import com.c206.backend.domain.quest.entity.Quest;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MemberQuestListResponseDto {

    private String petNickname;

    private Long memberQuestId;

    private Long questId;

    private int goal;

    private int progress;

    private boolean isDone;

}
