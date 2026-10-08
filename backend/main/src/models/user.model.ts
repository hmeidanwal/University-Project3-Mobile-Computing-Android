import {model, property, belongsTo, Entity} from '@loopback/repository';
import {Account, AccountWithRelations} from '.';

@model({settings: {strict: false}})
export class User extends Entity {

  
  @property({
    type: 'number',
    id: true,
    generated: true,
    postgresql: {
      columnName: 'id',
      dataType: 'integer',
      dataLength: null,
      dataPrecision: null,
      dataScale: null,
      nullable: 'NO',
    },
  })
  id?: number;

  @property({
    type: 'string',
    required: true,
  })
  fcm_token: string;

  @property({
    type: 'boolean',
    required: true,
  })
  notifications: boolean;

  @property({
    type: 'string',
  })
  picture?: string;

  @belongsTo(() => Account, {name: 'account', keyTo: 'email'})
  account_email: string;

  // Define well-known properties here

  // Indexer property to allow additional data
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  [prop: string]: any;

  constructor(data?: Partial<User>) {
    super(data);
  }
}

export interface UserRelations {
  account?: AccountWithRelations;
}

export type UserWithRelations = User & UserRelations;
