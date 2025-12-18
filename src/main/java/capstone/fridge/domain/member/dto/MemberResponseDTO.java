package capstone.fridge.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class MemberResponseDTO {

    @Builder
    @Getter
    @AllArgsConstructor
    public static class UserInfoDTO {
        private Long memberId;
        private String nickname;
        private String email;
        private String profileImageUrl;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    public static class UserPreferencesDTO {
        private List<String> allergies;
        private List<String> dislikes;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    public static class UserScrapsDTO {
        private List<ScrapRecipeDTO> scrapList;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    public static class ScrapRecipeDTO {
        private Long recipeId;
        private String title;
        private String thumbnailUrl;
    }
}
