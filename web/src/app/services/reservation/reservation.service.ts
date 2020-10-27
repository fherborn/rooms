import {Injectable} from '@angular/core';
import {forkJoin, Observable, of} from 'rxjs';
import {ReservationListEntity, SimpleReservation} from '../../models/reservation.model';
import {BackendService, TokenRequirement} from '../backend/backend.service';
import {map} from 'rxjs/operators';
import {SessionService} from '../session/session.service';
import {RichReservation} from '../../core/models/reservation.model';
import {Projection} from '../../core/projections.model';

@Injectable({
  providedIn: 'root'
})
export class ReservationService {

  private static createProtocol(reservation: SimpleReservation) {
    return {
      ...reservation,
      ...{
        room: reservation.room._links.self.href,
        user: reservation.user._links.self.href,
        abo: reservation.abo._links.self.href,
      }
    };
  }

  constructor(
    private readonly backendService: BackendService,
    private readonly sessionService: SessionService
  ) {
  }

  getAll(): Observable<RichReservation[]> {
    return this.backendService.getCollection<RichReservation>('reservations', 'reservations', {
      projection: Projection.RICH
    });
  }

  getSimpleByUser(userId: string): Observable<SimpleReservation[]> {
    return this.backendService.get<ReservationListEntity<SimpleReservation>>(`users/${userId}/reservations?projection=simple`, TokenRequirement.REQUIRED)
      .pipe(map(it => it._embedded.reservations));
  }

  save(reservation: SimpleReservation): Observable<SimpleReservation> {
    return this.backendService.post('reservations', ReservationService.createProtocol(reservation), TokenRequirement.REQUIRED);
  }

  getSimpleWhereAdmin(): Observable<SimpleReservation[]> {
    return of([]);
  //   return forkJoin(
  //     [
  //       this.sessionService.getSession(),
  //       this.backendService.get<ReservationListEntity<SimpleReservation>>('reservations?projection=simple', TokenRequirement.REQUIRED)
  //         .pipe(map(it => it._embedded.reservations))
  //     ]
  //   ).pipe(
  //     map(([session, result]) => {
  //       return result
  //         .filter(item => session.acl.find(aclEntry =>
  //           aclEntry.id === item.id
  //           && (aclEntry.permission === 'create' || aclEntry.permission === 'update')
  //           ) !== null
  //         );
  //     })
  //   );
  }
}
