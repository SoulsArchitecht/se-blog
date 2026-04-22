import { Injectable, inject } from "@angular/core";
import { AuthService } from "./auth.service";
import { UserProfileService } from './user-profile.service';

@Injectable({ providedIn: 'root' })
export class AppInitService {
    private authService = inject(AuthService);
    private UserProfileService = inject(UserProfileService);

    init(): void {
        if (this.authService.isAuthenticated()) {
            this.UserProfileService.getProfile().subscribe();
        }
    }
}